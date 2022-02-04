#!/bin/sh

echo Clean
rm -rf customjre
rm -rf cls

#compile the class files in schedsplitter
echo Compile
javac --module-path ./libraries/javafx-sdk-11.0.2/lib --module schedsplitter -d ./cls --module-source-path .

echo Copy resources
#copy the java fx resources into the class output folder
cp ./schedsplitter/openjfx/*.fxml ./cls/schedsplitter/openjfx/.
cp ./schedsplitter/openjfx/*.css ./cls/schedsplitter/openjfx/.

echo Build custom JRE
#build a custom jre including the applications module
/Library/Java/JavaVirtualMachines/jdk-14.0.2.jdk/Contents/Home/bin/jlink --module-path /Users/nealsnooke/RESEARCH/PROTOTYPES/TOOLS/javafx-jmods-11.0.2:./cls --add-modules schedsplitter --output customjre


