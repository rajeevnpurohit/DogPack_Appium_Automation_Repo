@echo off
REM ============================================================
REM  DogPack Full Run - Multi iterations
REM  Run from the PROJECT ROOT (the folder containing pom.xml).
REM  Produces reports\NineHertzReport_1.html ... _5.html
REM ============================================================

for /L %%i in (1,1,1) do (
  echo(
  echo ===== Iteration %%i =====
  if exist reports\NineHertzReport.html del /Q reports\NineHertzReport.html
  call mvn test -P FullRun -Dtester="Shubham Mathur"
  if exist reports\NineHertzReport.html (
    move /Y reports\NineHertzReport.html reports\NineHertzReport_%%i.html
  ) else (
    echo [WARN] No report produced for iteration %%i ^(run may have failed^).
  )
)

echo(
echo ===== All iterations complete. See reports\NineHertzReport_1..5.html =====
