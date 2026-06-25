package org.rahulshettyacademy.TestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * NineHertzReport - a self-contained modern HTML run report.
 *
 * <p>Collects results in parallel with ExtentReports (it taps the SAME three
 * data sources the {@code Listeners} class already produces - lifecycle events,
 * tagged {@code System.out} action lines, and the per-test screenshot path) and,
 * at suite end, writes a single self-contained file to
 * {@code reports/NineHertzReport.html}.
 *
 * <p>Design: dark ink top bar + light work surface, run-summary strip
 * (pass-rate donut, totals, duration, device, tester), a Features sidebar, and
 * per-test-case step cards whose actions render as a status-rail timeline. The
 * stack trace for a failing case is a full-width dark panel.
 *
 * <p>Vocabulary follows the ExtentReports model used elsewhere in this project:
 * Features (the test classes) -> Test cases (the {@code @Test} methods) ->
 * Log events (the action lines).
 *
 * <p>The output is fully portable: screenshots and the company logo are
 * embedded as base64 data URIs, so the HTML opens anywhere with no asset
 * folder. The logo is loaded best-effort from
 * {@code src/test/resources/ninehertz_logo.png} (or {@code reports/}); if
 * absent, a text "9HZ" badge is used instead.
 *
 * <p>Every public method is defensive - a reporting failure must never break
 * the test run.
 */
public final class NineHertzReport {

    private NineHertzReport() { }

    // ---------------------------------------------------------------- config
    private static final String REPORT_TITLE = "Nine Hertz Test Automation Report";
    private static final String RUN_NAME = "Appium Automation Results";
    private static String tester = "Shubham mathur";
    private static String device = "moto g45";
    private static String osVersion = "Android 14";

    /** Set environment facts shown in the summary strip. Any null is ignored. */
    public static void setEnv(String tester_, String device_, String os_) {
        if (tester_ != null) tester = tester_;
        if (device_ != null) device = device_;
        if (os_ != null) osVersion = os_;
    }

    // ----------------------------------------------------------- data model
    private static final class Ev {
        final String tag, text, level, time;
        Ev(String tag, String text, String level, String time) {
            this.tag = tag; this.text = text; this.level = level; this.time = time;
        }
    }

    private static final class TC {
        String name;
        String status = "pass";   // pass | fail | skip
        long start;
        long durMs;
        final List<Ev> events = new ArrayList<>();
        String shotB64;           // data URI or null
        String trace;             // stack trace or null
    }

    private static final class Feature {
        String name;
        final List<TC> cases = new ArrayList<>();
    }

    private static final Map<String, Feature> features = new LinkedHashMap<>();
    private static final ThreadLocal<TC> current = new ThreadLocal<>();
    private static long runStart = 0L;
    private static long runEnd = 0L;

    // -------------------------------------------------------------- collect
    /** Begin a test case under the given feature (class). */
    public static synchronized void startTest(String feature, String method) {
        try {
            Feature f = features.get(feature);
            if (f == null) {
                f = new Feature();
                f.name = feature;
                features.put(feature, f);
            }
            TC tc = new TC();
            tc.name = method;
            tc.start = System.currentTimeMillis();
            if (runStart == 0L) runStart = tc.start;
            f.cases.add(tc);
            current.set(tc);
        } catch (Exception ignore) { }
    }

    /** Append a tagged action line to the current test case as a log event. */
    public static void logEvent(String line) {
        try {
            TC tc = current.get();
            if (tc == null || line == null) return;
            String s = line.trim();
            if (s.isEmpty()) return;
            String up = s.toUpperCase();
            String level;
            if (up.startsWith("[ASSERT FAIL]") || up.startsWith("[FAIL]")
                    || up.startsWith("[ERROR]") || up.contains("EXCEPTION")) {
                level = "fail";
            } else if (up.startsWith("[WARN]") || up.startsWith("[WARNING]")) {
                level = "warn";
            } else if (s.startsWith("[ACTION]") || s.startsWith("[INPUT]")
                    || s.startsWith("[INFO]") || s.startsWith("[FLOW]")
                    || s.startsWith("[OK]") || s.startsWith("[STEP]")
                    || s.startsWith("[ASSERT")) {
                level = "pass";
            } else {
                return; // untagged lifecycle noise
            }
            String tag = "";
            String text = s;
            int rb = s.indexOf(']');
            if (s.startsWith("[") && rb > 0) {
                tag = s.substring(0, rb + 1);
                text = s.substring(rb + 1).trim();
            }
            tc.events.add(new Ev(tag, text, level, hms(System.currentTimeMillis())));
        } catch (Exception ignore) { }
    }

    /** Finish the current test case. status = pass | fail | skip. */
    public static synchronized void endTest(String feature, String method,
                                            String status, String trace, String shotPath) {
        try {
            TC tc = current.get();
            if (tc == null) {           // skipped without a start (dependsOnMethods chain)
                startTest(feature, method);
                tc = current.get();
            }
            if (tc == null) return;
            tc.status = (status == null) ? "pass" : status;
            tc.durMs = System.currentTimeMillis() - tc.start;
            runEnd = System.currentTimeMillis();
            if (trace != null && !trace.isEmpty()) tc.trace = trace;
            if (shotPath != null) tc.shotB64 = encodeImg(shotPath);
        } catch (Exception ignore) {
        } finally {
            current.remove();
        }
    }

    /** Convert a Throwable to a full stack-trace string (for the trace panel). */
    public static String stackToString(Throwable t) {
        if (t == null) return null;
        try {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        } catch (Exception e) {
            return String.valueOf(t.getMessage());
        }
    }

    // --------------------------------------------------------------- render
    /** Write reports/NineHertzReport.html. Safe to call once at suite end. */
    public static synchronized void render() {
        try {
            // Tester name is customizable at run time:
            //   mvn test -Dtester="Your Name"        (system property)
            //   TESTER="Your Name" mvn test          (environment variable)
            String tProp = System.getProperty("tester");
            if (tProp == null || tProp.trim().isEmpty()) {
                tProp = System.getenv("TESTER");
            }
            if (tProp != null && !tProp.trim().isEmpty()) {
                tester = tProp.trim();
            }

            int totalCases = 0, passed = 0, failed = 0, skipped = 0;
            for (Feature f : features.values()) {
                for (TC tc : f.cases) {
                    totalCases++;
                    if ("fail".equals(tc.status)) failed++;
                    else if ("skip".equals(tc.status)) skipped++;
                    else passed++;
                }
            }
            int totalFeatures = features.size();
            int passPct = totalCases == 0 ? 0 : Math.round(passed * 100f / totalCases);
            int failPct = totalCases == 0 ? 0 : Math.round(failed * 100f / totalCases);
            long runDur = (runEnd > runStart) ? (runEnd - runStart) : 0L;

            StringBuilder h = new StringBuilder(1 << 16);
            h.append("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"/>")
             .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>")
             .append("<title>").append(esc(REPORT_TITLE)).append("</title>")
             .append("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">")
             .append("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>")
             .append("<link href=\"https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@500;600;700&family=Inter:wght@400;500;600&family=JetBrains+Mono:wght@400;500&display=swap\" rel=\"stylesheet\">")
             .append("<style>").append(CSS).append("</style></head><body>");

            // ---- top bar
            String logo = encodeLogo();
            h.append("<header class=\"topbar\"><div class=\"logo\">");
            if (logo != null) {
                h.append("<img class=\"mark\" src=\"").append(logo).append("\" alt=\"Nine Hertz\"/>");
            } else {
                h.append("<span class=\"mark txt\">9HZ</span>");
            }
            h.append(' ').append(esc(REPORT_TITLE)).append("</div><div class=\"spacer\"></div>")
             .append("<span class=\"pill run\">").append(esc(RUN_NAME)).append("</span>")
             .append("<span class=\"pill ts\">").append(esc(stamp(runStart))).append("</span></header>");

            // ---- summary strip
            h.append("<section class=\"summary\">");
            h.append("<div class=\"scard donutcard\"><div class=\"donut\" style=\"")
             .append("background:conic-gradient(var(--pass) 0 ").append(passPct).append("%, ")
             .append("var(--fail) ").append(passPct).append("% ").append(passPct + failPct).append("%, ")
             .append("var(--line) ").append(passPct + failPct).append("% 100%)\">")
             .append("<span class=\"val\">").append(passPct).append("%</span></div>")
             .append("<div><div class=\"label\">Pass rate</div><div class=\"legend\" style=\"margin-top:8px\">")
             .append("<span><i class=\"dot pass\"></i> ").append(passed).append(" passed</span>")
             .append("<span><i class=\"dot fail\"></i> ").append(failed).append(" failed</span>")
             .append("<span><i class=\"dot skip\"></i> ").append(skipped).append(" skipped</span>")
             .append("</div></div></div>");
            h.append(card("Total features", String.valueOf(totalFeatures), totalCases + " test cases total"));
            h.append(card("Duration", fmtDur(runDur), "m : s"));
            h.append(card("Device", device, esc(osVersion)));
            h.append(card("Tester", tester, "local run"));
            h.append("</section>");

            // ---- main split
            h.append("<div class=\"main\"><aside class=\"side\"><div class=\"head\"><span>Features</span><span>")
             .append(totalFeatures).append("</span></div>");

            int idx = 0;
            for (Feature f : features.values()) {
                int fp = 0, ff = 0;
                long fdur = 0;
                for (TC tc : f.cases) {
                    if ("fail".equals(tc.status)) ff++;
                    else if (!"skip".equals(tc.status)) fp++;
                    fdur += tc.durMs;
                }
                String dotCls = ff > 0 ? "fail" : "pass";
                h.append("<div class=\"tc").append(idx == 0 ? " active" : "").append("\" data-idx=\"")
                 .append(idx).append("\"><span class=\"sdot ").append(dotCls).append("\"></span><div>")
                 .append("<div class=\"tc-name\">").append(esc(f.name)).append("</div>")
                 .append("<div class=\"tc-meta\"><b>").append(fp).append(" \u2713</b>");
                if (ff > 0) h.append("<i>").append(ff).append(" \u2715</i>");
                h.append("<span>\u00b7 ").append(f.cases.size()).append(" test cases</span>")
                 .append("<span>\u00b7 ").append(fmtDur(fdur)).append("</span></div></div></div>");
                idx++;
            }
            h.append("</aside><div class=\"panels\">");

            // ---- one panel per feature
            idx = 0;
            for (Feature f : features.values()) {
                int fp = 0, ff = 0;
                long fdur = 0;
                for (TC tc : f.cases) {
                    if ("fail".equals(tc.status)) ff++;
                    else if (!"skip".equals(tc.status)) fp++;
                    fdur += tc.durMs;
                }
                h.append("<section class=\"panel\" data-idx=\"").append(idx).append("\"")
                 .append(idx == 0 ? "" : " style=\"display:none\"").append(">");
                h.append("<div class=\"panel-head\"><h2>").append(esc(f.name)).append("</h2>")
                 .append("<div class=\"chips\"><span class=\"chip ").append(ff > 0 ? "f" : "t").append("\">")
                 .append(fp).append(" / ").append(f.cases.size()).append(" passed</span>")
                 .append("<span class=\"chip dur\">").append(fmtDur(fdur)).append("</span></div></div>");

                for (TC tc : f.cases) {
                    boolean fail = "fail".equals(tc.status);
                    boolean skip = "skip".equals(tc.status);
                    String badge = fail ? "fail" : (skip ? "skip" : "pass");
                    String mark = fail ? "\u2715" : (skip ? "\u2013" : "\u2713");
                    h.append("<details class=\"step\"").append(fail ? " open" : "").append("><summary>")
                     .append("<span class=\"badge ").append(badge).append("\">").append(mark).append("</span>")
                     .append("<span class=\"stitle\">").append(esc(tc.name)).append("</span>")
                     .append("<span class=\"sright\"><span>").append(tc.events.size())
                     .append(" log events</span><span class=\"chev\">\u25b6</span></span></summary>");
                    h.append("<div class=\"body\">");

                    // rail timeline
                    h.append("<div class=\"rail\">");
                    if (tc.events.isEmpty()) {
                        h.append("<div class=\"ev\"><span class=\"m info\">\u00b7</span>")
                         .append("<span class=\"txt\">No log events captured.</span></div>");
                    } else {
                        for (Ev e : tc.events) {
                            String m = "fail".equals(e.level) ? "fail"
                                    : ("warn".equals(e.level) ? "info" : "pass");
                            String mk = "fail".equals(e.level) ? "\u2715"
                                    : ("warn".equals(e.level) ? "!" : "\u2713");
                            h.append("<div class=\"ev\"><span class=\"m ").append(m).append("\">")
                             .append(mk).append("</span><span class=\"txt\">");
                            if (!e.tag.isEmpty()) {
                                h.append("<span class=\"tag").append("fail".equals(e.level) ? " tf" : "")
                                 .append("\">").append(esc(e.tag)).append("</span>");
                            }
                            h.append(esc(e.text)).append("<span class=\"time\">").append(esc(e.time))
                             .append("</span></span></div>");
                        }
                    }
                    h.append("</div>");

                    // screenshot
                    if (tc.shotB64 != null) {
                        h.append("<div class=\"shot\"><img src=\"").append(tc.shotB64)
                         .append("\" alt=\"\"/><div class=\"cap\">").append(esc(tc.name))
                         .append(".png</div></div>");
                    } else {
                        h.append("<div class=\"shot\"><div class=\"ph\">no screenshot</div></div>");
                    }

                    // stack trace
                    if (tc.trace != null) {
                        h.append("<div class=\"trace\"><div class=\"lbl\">Stack trace</div><pre>")
                         .append(esc(tc.trace)).append("</pre></div>");
                    }

                    h.append("</div></details>");
                }
                h.append("</section>");
                idx++;
            }

            h.append("</div></div>");
            h.append("<p class=\"note\">").append(esc(REPORT_TITLE)).append(" \u00b7 generated ")
             .append(esc(stamp(System.currentTimeMillis()))).append("</p>");

            // ---- interactivity
            h.append("<script>")
             .append("document.querySelectorAll('.tc').forEach(function(t){")
             .append("t.addEventListener('click',function(){")
             .append("document.querySelectorAll('.tc').forEach(function(x){x.classList.remove('active')});")
             .append("t.classList.add('active');")
             .append("var i=t.getAttribute('data-idx');")
             .append("document.querySelectorAll('.panel').forEach(function(p){")
             .append("p.style.display=(p.getAttribute('data-idx')===i)?'flex':'none';});")
             .append("});});")
             .append("</script>");

            h.append("</body></html>");

            Path dir = Paths.get(System.getProperty("user.dir"), "reports");
            Files.createDirectories(dir);
            Path out = dir.resolve("NineHertzReport.html");
            Files.write(out, h.toString().getBytes(StandardCharsets.UTF_8));
            System.out.println("[OK]   Nine Hertz report written: " + out);
        } catch (Exception e) {
            System.out.println("[WARN] Nine Hertz report generation failed (non-fatal): "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // -------------------------------------------------------------- helpers
    private static String card(String label, String big, String sub) {
        return "<div class=\"scard\"><div class=\"label\">" + esc(label)
                + "</div><div class=\"big\">" + esc(big) + "</div><div class=\"sub\">"
                + esc(sub) + "</div></div>";
    }

    private static String encodeImg(String path) {
        try {
            byte[] b = Files.readAllBytes(Paths.get(path));
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(b);
        } catch (Exception e) {
            return null;
        }
    }

    private static String encodeLogo() {
        String[] candidates = {
            Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "ninehertz_logo.png").toString(),
            Paths.get(System.getProperty("user.dir"), "reports", "ninehertz_logo.png").toString(),
            Paths.get(System.getProperty("user.dir"), "ninehertz_logo.png").toString()
        };
        for (String c : candidates) {
            try {
                Path p = Paths.get(c);
                if (Files.exists(p)) {
                    return "data:image/png;base64,"
                            + Base64.getEncoder().encodeToString(Files.readAllBytes(p));
                }
            } catch (Exception ignore) { }
        }
        return null;
    }

    private static String hms(long ms) {
        return new SimpleDateFormat("HH:mm:ss").format(new Date(ms));
    }

    private static String stamp(long ms) {
        if (ms <= 0) ms = System.currentTimeMillis();
        return new SimpleDateFormat("MMM dd, yyyy \u00b7 hh:mm a").format(new Date(ms));
    }

    private static String fmtDur(long ms) {
        long s = ms / 1000;
        long m = s / 60;
        s = s % 60;
        if (m > 0) return m + "m " + (s < 10 ? "0" + s : s) + "s";
        return s + "s";
    }

    private static String esc(String x) {
        if (x == null) return "";
        return x.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    // ------------------------------------------------------------------ CSS
    private static final String CSS =
        ":root{--ink:#0e1a22;--ink-2:#46606e;--ink-3:#7d97a4;--surface:#eef3f5;--card:#fff;"
        + "--line:#e2e9ec;--sidebar:#1060c0;--sidebar-line:#21404f;--brand:#0d9488;"
        + "--brand-soft:#d6f3ee;--pass:#1aa05a;--pass-soft:#e4f6ec;--fail:#e11d48;"
        + "--fail-soft:#fde7ec;--warn:#d97706;--radius:14px;"
        + "--shadow:0 1px 2px rgba(14,26,34,.04),0 8px 24px rgba(14,26,34,.06);"
        + "--mono:'JetBrains Mono',ui-monospace,Menlo,monospace;}"
        + "*{box-sizing:border-box}html,body{margin:0}"
        + "body{font-family:'Inter',system-ui,sans-serif;color:var(--ink);background:var(--surface);"
        + "-webkit-font-smoothing:antialiased;line-height:1.5}"
        + "h1,h2,h3,.disp{font-family:'Space Grotesk','Inter',sans-serif;letter-spacing:-.01em}"
        + ".topbar{position:sticky;top:0;z-index:30;display:flex;align-items:center;gap:16px;"
        + "padding:14px 22px;background:var(--sidebar);color:#eaf3f4;border-bottom:1px solid #0c4f9e}"
        + ".logo{display:flex;align-items:center;gap:10px;font-family:'Space Grotesk';font-weight:700;font-size:15px}"
        + ".logo .mark{height:30px;width:auto;border-radius:7px;background:#fff;padding:3px 7px;object-fit:contain;display:block}"
        + ".logo .mark.txt{display:grid;place-items:center;height:28px;width:auto;padding:0 10px;color:var(--brand);font-size:13px;letter-spacing:.04em}"
        + ".topbar .spacer{flex:1}"
        + ".pill{padding:6px 12px;border-radius:999px;font-size:12.5px;font-weight:600}"
        + ".pill.run{background:rgba(255,255,255,.16);color:#fff;border:1px solid rgba(255,255,255,.3)}"
        + ".pill.ts{background:rgba(255,255,255,.06);color:#bcd2d8;border:1px solid var(--sidebar-line)}"
        + ".summary{display:grid;grid-template-columns:auto repeat(4,1fr);gap:14px;padding:18px 22px}"
        + ".scard{background:var(--card);border:1px solid var(--line);border-radius:var(--radius);padding:16px 18px;box-shadow:var(--shadow)}"
        + ".scard .label{font-size:11px;letter-spacing:.08em;text-transform:uppercase;color:var(--ink-3);font-weight:600}"
        + ".scard .big{font-family:'Space Grotesk';font-weight:700;font-size:24px;margin-top:6px;word-break:break-word}"
        + ".scard .sub{font-size:12.5px;color:var(--ink-2);margin-top:2px}"
        + ".donutcard{display:flex;align-items:center;gap:16px;min-width:230px}"
        + ".donut{width:84px;height:84px;border-radius:50%;flex:none;display:grid;place-items:center;position:relative}"
        + ".donut::after{content:\"\";position:absolute;inset:11px;background:var(--card);border-radius:50%}"
        + ".donut .val{position:relative;font-family:'Space Grotesk';font-weight:700;font-size:20px}"
        + ".legend{display:flex;flex-direction:column;gap:6px;font-size:12.5px;color:var(--ink-2)}"
        + ".legend span{display:inline-flex;align-items:center;gap:7px}"
        + ".dot{width:9px;height:9px;border-radius:3px;display:inline-block}"
        + ".dot.pass{background:var(--pass)}.dot.fail{background:var(--fail)}.dot.skip{background:var(--ink-3)}"
        + ".main{display:grid;grid-template-columns:320px 1fr;gap:14px;padding:0 22px 28px;align-items:start}"
        + ".side{background:var(--card);border:1px solid var(--line);border-radius:var(--radius);overflow:hidden;box-shadow:var(--shadow);position:sticky;top:78px}"
        + ".side .head{padding:14px 16px;border-bottom:1px solid var(--line);font-size:11px;letter-spacing:.08em;text-transform:uppercase;color:var(--ink-3);font-weight:600;display:flex;justify-content:space-between;align-items:center}"
        + ".tc{display:flex;gap:12px;align-items:flex-start;padding:14px 16px;cursor:pointer;border-left:3px solid transparent;border-bottom:1px solid var(--line);transition:background .15s}"
        + ".tc:hover{background:#f5f9fa}"
        + ".tc.active{background:linear-gradient(90deg,var(--brand-soft),#fff);border-left-color:var(--brand)}"
        + ".tc.active .tc-name{color:var(--brand)}"
        + ".tc .sdot{width:10px;height:10px;border-radius:50%;margin-top:5px;flex:none}"
        + ".tc .sdot.pass{background:var(--pass);box-shadow:0 0 0 3px var(--pass-soft)}"
        + ".tc .sdot.fail{background:var(--fail);box-shadow:0 0 0 3px var(--fail-soft)}"
        + ".tc-name{font-weight:600;font-size:13.5px}"
        + ".tc-meta{font-size:12px;color:var(--ink-3);margin-top:3px;display:flex;gap:8px;flex-wrap:wrap}"
        + ".tc-meta b{color:var(--pass);font-weight:600}.tc-meta i{color:var(--fail);font-style:normal;font-weight:600}"
        + ".panels{min-width:0}"
        + ".panel{display:flex;flex-direction:column;gap:14px}"
        + ".panel-head{background:var(--card);border:1px solid var(--line);border-radius:var(--radius);padding:18px 20px;box-shadow:var(--shadow);display:flex;align-items:center;gap:14px;flex-wrap:wrap}"
        + ".panel-head h2{margin:0;font-size:19px}"
        + ".panel-head .chips{display:flex;gap:8px;margin-left:auto;flex-wrap:wrap}"
        + ".chip{font-size:12px;font-weight:600;padding:5px 11px;border-radius:8px}"
        + ".chip.t{background:var(--pass-soft);color:var(--pass)}"
        + ".chip.f{background:var(--fail-soft);color:var(--fail)}"
        + ".chip.dur{background:#eef2f4;color:var(--ink-2)}"
        + ".step{background:var(--card);border:1px solid var(--line);border-radius:var(--radius);box-shadow:var(--shadow);overflow:hidden}"
        + ".step>summary{list-style:none;cursor:pointer;display:flex;align-items:center;gap:13px;padding:15px 18px;user-select:none}"
        + ".step>summary::-webkit-details-marker{display:none}"
        + ".step .badge{width:24px;height:24px;border-radius:7px;display:grid;place-items:center;font-size:13px;font-weight:700;color:#fff;flex:none}"
        + ".step .badge.pass{background:var(--pass)}.step .badge.fail{background:var(--fail)}.step .badge.skip{background:var(--ink-3)}"
        + ".step .stitle{font-family:'Space Grotesk';font-weight:600;font-size:15px}"
        + ".step .sright{margin-left:auto;display:flex;align-items:center;gap:14px;color:var(--ink-3);font-size:12.5px}"
        + ".chev{transition:transform .2s;color:var(--ink-3)}.step[open] .chev{transform:rotate(90deg)}"
        + ".step .body{border-top:1px solid var(--line);padding:6px 18px 16px;display:grid;grid-template-columns:1fr 150px;gap:18px}"
        + ".rail{position:relative;margin-top:10px;min-width:0}"
        + ".rail::before{content:\"\";position:absolute;left:9px;top:6px;bottom:6px;width:2px;background:var(--line)}"
        + ".ev{position:relative;display:flex;gap:12px;padding:5px 0;align-items:flex-start}"
        + ".ev .m{width:20px;height:20px;border-radius:50%;flex:none;display:grid;place-items:center;font-size:11px;color:#fff;z-index:1;border:3px solid var(--card)}"
        + ".ev .m.pass{background:var(--pass)}.ev .m.fail{background:var(--fail)}.ev .m.info{background:var(--ink-3)}"
        + ".ev .txt{font-size:13px;padding-top:1px;word-break:break-word}"
        + ".ev .tag{font-family:var(--mono);font-size:11px;color:var(--brand);font-weight:500;margin-right:6px}"
        + ".ev .tag.tf{color:var(--fail)}"
        + ".ev .time{margin-left:8px;font-size:11px;color:var(--ink-3)}"
        + ".shot{border:1px solid var(--line);border-radius:10px;overflow:hidden;height:fit-content}"
        + ".shot img{display:block;width:100%;height:auto}"
        + ".shot .ph{height:200px;background:repeating-linear-gradient(45deg,#f0f4f5 0 10px,#e9eff1 10px 20px);display:grid;place-items:center;color:var(--ink-3);font-size:11px}"
        + ".shot .cap{font-size:11px;color:var(--ink-3);padding:7px 9px;border-top:1px solid var(--line);text-align:center;word-break:break-word}"
        + ".trace{grid-column:1 / -1;margin-top:6px}"
        + ".trace .lbl{font-size:11px;letter-spacing:.06em;text-transform:uppercase;color:var(--fail);font-weight:700;margin-bottom:7px}"
        + ".trace pre{font-family:var(--mono);font-size:12.5px;line-height:1.65;color:#cfe3e8;background:#0e1f29;border:1px solid var(--sidebar-line);border-radius:10px;padding:16px 18px;margin:0;width:100%;min-height:160px;overflow:auto;white-space:pre;resize:vertical}"
        + ".note{padding:12px 22px;color:var(--ink-3);font-size:12px;text-align:center}"
        + "@media (max-width:920px){.summary{grid-template-columns:1fr 1fr}.main{grid-template-columns:1fr}.side{position:static}.step .body{grid-template-columns:1fr}}"
        + "@media (prefers-reduced-motion:reduce){*{transition:none!important}}";
}