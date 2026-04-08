#!/bin/bash
# ============================================================
#  Digital Library Management System - Build & Run Script
# ============================================================

echo "╔══════════════════════════════════════════════════════╗"
echo "║     Digital Library Management System               ║"
echo "║     Java Course Project - All 6 Units Covered       ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

# Create output directory
mkdir -p out data

# Compile all Java files
echo "► Compiling Java source files..."
javac -d out \
  src/models/Book.java \
  src/models/Member.java \
  src/models/PremiumMember.java \
  src/models/StudentMember.java \
  src/exceptions/LibraryException.java \
  src/main/LibraryFileManager.java \
  src/main/LibraryInterfaces.java \
  src/main/LibraryReport.java \
  src/threads/LibraryThreadManager.java \
  src/main/LibraryCatalog.java \
  src/gui/LibraryGUI.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo ""
    echo "► Launching Digital Library GUI..."
    java -cp out gui.LibraryGUI
else
    echo "❌ Compilation failed. Please check errors above."
    exit 1
fi
