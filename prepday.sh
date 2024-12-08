#!/bin/bash

# Check if a day number is provided as an argument
if [ $# -ne 1 ]; then
    echo "Usage: $0 <day-number>"
    exit 1
fi

# Get the day number from the input
DAY=$1

# Check if the input is a valid number
if ! [[ "$DAY" =~ ^[0-9]+$ ]]; then
    echo "Error: Day must be a number."
    exit 1
fi

# Define folder and file paths
SRC_FOLDER="src"
KT_FILE="${SRC_FOLDER}/Day${DAY}.kt"
TXT_FILE="${SRC_FOLDER}/Day${DAY}.txt"
TEST_FILE="${SRC_FOLDER}/Day${DAY}_test.txt"
TEMPLATE_FILE="${SRC_FOLDER}/DayX.kt"

# Ensure the src folder exists
if [ ! -d "$SRC_FOLDER" ]; then
    echo "Error: Source folder '$SRC_FOLDER' does not exist."
    exit 1
fi

# Copy DayX.kt template and replace placeholders with the input day number
if [ -f "$TEMPLATE_FILE" ]; then
    cp "$TEMPLATE_FILE" "$KT_FILE"
    # Replace placeholders (compatible with macOS and Linux)
    sed -i '' "s/DayX_test/Day${DAY}_test/g" "$KT_FILE" 2>/dev/null || sed -i "s/DayX_test/Day${DAY}_test/g" "$KT_FILE"
    sed -i '' "s/DayX/Day${DAY}/g" "$KT_FILE" 2>/dev/null || sed -i "s/DayX/Day${DAY}/g" "$KT_FILE"
    echo "Created $KT_FILE with placeholders replaced."
else
    echo "Error: Template file $TEMPLATE_FILE does not exist in $SRC_FOLDER."
    exit 1
fi

# Create empty .txt files in the src folder
touch "$TXT_FILE"
echo "Created $TXT_FILE"

touch "$TEST_FILE"
echo "Created $TEST_FILE"

echo "All files for Day $DAY created successfully in $SRC_FOLDER."
