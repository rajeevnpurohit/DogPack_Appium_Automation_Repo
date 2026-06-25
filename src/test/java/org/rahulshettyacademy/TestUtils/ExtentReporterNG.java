package org.rahulshettyacademy.TestUtils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReporterNG {
	static ExtentReports extent;
	
	public static ExtentReports getReporterObject()
	{
		
	//	ExtentReports , ExtentSparkReporter
		String path =System.getProperty("user.dir")+"//reports//index.html";
		ExtentSparkReporter reporter = new ExtentSparkReporter(path);
		reporter.config().setReportName("Appium Automation Results");
		reporter.config().setDocumentTitle("DockPack");
		// Darken the selected/active test item in the left sidebar so it's clearly visible.
		reporter.config().setCSS(
				".test-item.active, li.test-item.active {"
				+ " background-color: #1e3a5f !important; border-left: 4px solid #4a90d9 !important;"
				+ " font-weight: bold !important; }"
				+ " .test-item.active, .test-item.active * { color: #ffffff !important; }"
				+ " textarea.code-block { width: 100% !important; min-height: 260px !important;"
				+ " resize: both !important; white-space: pre !important; font-family: monospace !important; }");
		// Bulletproof fallback: re-apply inline !important styles to the selected
		// (active) sidebar item every 300ms, so it stays clearly highlighted
		// regardless of the theme's own CSS.
		reporter.config().setJS(
				"setInterval(function(){"
				+ "document.querySelectorAll('.test-item').forEach(function(el){"
				+ "if(el.classList.contains('active')){"
				+ "el.style.setProperty('background-color','#1e3a5f','important');"
				+ "el.style.setProperty('border-left','5px solid #4a90d9','important');"
				+ "el.style.setProperty('font-weight','bold','important');"
				+ "el.style.setProperty('color','#ffffff','important');"
				+ "}else{"
				+ "el.style.removeProperty('background-color');"
				+ "el.style.removeProperty('border-left');"
				+ "el.style.removeProperty('font-weight');"
				+ "el.style.removeProperty('color');"
				+ "}});"
				+ "},300);");
		
		 extent =new ExtentReports();
		extent.attachReporter(reporter);
		extent.setSystemInfo("Tester", "Shubham mathur");
		return extent;
		
	}

	
}