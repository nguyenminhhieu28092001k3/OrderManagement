#!/bin/bash

echo "========================================"
echo "   Testing Database Connection"
echo "========================================"
echo

echo "[1] Compiling test program..."
javac -cp "postgresql-42.7.6.jar" database/TestConnection.java

if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed!"
    exit 1
fi

echo "[SUCCESS] Compilation completed!"
echo

echo "[2] Running connection test..."
java -cp "postgresql-42.7.6.jar:database" TestConnection

echo
echo "Cleaning up..."
rm -f database/TestConnection.class

echo 