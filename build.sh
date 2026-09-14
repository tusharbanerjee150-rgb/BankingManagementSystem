#!/bin/bash

echo "======================================"
echo "  Banking Management System - Build"
echo "======================================"

rm -rf out
mkdir -p out

echo
echo "Compiling source files..."

javac -d out \
src/com/banking/cli/Main.java \
src/com/banking/model/*.java \
src/com/banking/service/*.java \
src/com/banking/validation/*.java

if [ $? -ne 0 ]; then
    echo
    echo "======================================"
    echo "         BUILD FAILED"
    echo "======================================"
    exit 1
fi

echo
echo "======================================"
echo "         BUILD SUCCESSFUL"
echo "======================================"