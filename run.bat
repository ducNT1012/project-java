
@echo off
cd /d "%~dp0"

if not exist target\classes mkdir target\classes

javac -cp "target\classes;C:\Users\os\.m2\repository\com\microsoft\sqlserver\mssql-jdbc\13.2.0.jre11\mssql-jdbc-13.2.0.jre11.jar" -d target\classes src\main\java\com\example\model\*.java src\main\java\com\example\util\*.java src\main\java\com\example\dao\*.java src\main\java\com\example\view\*.java

if errorlevel 1 (
    pause
    exit /b
)

java -cp "target\classes;C:\Users\os\.m2\repository\com\microsoft\sqlserver\mssql-jdbc\13.2.0.jre11\mssql-jdbc-13.2.0.jre11.jar" com.example.view.LoginFrame

pause
