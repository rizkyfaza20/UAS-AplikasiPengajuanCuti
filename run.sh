#!/bin/bash
# PT BSF Employee Leave Application - Build & Launch Script

# Ensure libraries directory exists
mkdir -p lib
mkdir -p bin

# Verify library jar downloads
if [ ! -f "lib/mysql-connector-j-8.3.0.jar" ]; then
    echo "Downloading MySQL Connector/J..."
    curl -L -o lib/mysql-connector-j-8.3.0.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar
fi


# Clean previous build artifacts
echo "Cleaning old build files..."
rm -rf bin/*

# Compile all source files
echo "Compiling Java source files..."
javac -cp "lib/*" -d bin $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "Compilation successful! Starting PT BSF Leave Application..."
    # Launch application
    java -cp "bin:lib/*" com.bsf.leaveapp.Main
else
    echo "Compilation failed. Please inspect build errors."
    exit 1
fi
