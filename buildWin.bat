echo run dot_clean on the mac for the remote folder 
echo to remove the pesky ._ metadata files

echo Clean
rd /s /q "customjre"
rd /s /q "cls"

echo Compile the class files in schedsplitter
javac --module-path .\libraries\javafx-sdk-17.0.0.1\lib --module schedsplitter -d .\cls --module-source-path .

echo Copy the java fx resources into the class output folder
copy .\schedsplitter\openjfx\*.fxml .\cls\schedsplitter\openjfx\.
copy .\schedsplitter\openjfx\*.css .\cls\schedsplitter\openjfx\.

echo Build a custom jre including the applications module
jlink --module-path .\libraries\javafx-jmods-17.0.0.1;./cls --add-modules schedsplitter --output customjre

echo run using ".\customjre\bin\java --module schedsplitter/openjfx.MainApp"
