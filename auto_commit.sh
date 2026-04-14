#!/bin/bash

# A simple script to automate running tests, compiling, and pushing to git.

echo "========================================="
echo " Building and Testing Project            "
echo "========================================="
./mvnw clean test

if [ $? -ne 0 ]; then
  echo "❌ Tests failed! Aborting commit."
  exit 1
fi
echo "✅ Tests passed successfully!"

echo "========================================="
echo " Git Add and Commit                      "
echo "========================================="
git add -A

# Prompt user for a commit message, or use a default one
read -p "Enter commit message (Press Enter to use default 'chore: auto commit from script'): " COMMIT_MSG

if [ -z "$COMMIT_MSG" ]; then
    COMMIT_MSG="chore: auto commit from script"
fi

git commit -m "$COMMIT_MSG"
echo "✅ Changes committed locally."

echo "========================================="
echo " Pushing to Remote Repository            "
echo "========================================="
git push origin HEAD

if [ $? -ne 0 ]; then
  echo "❌ Push failed! You might need to update your GitHub PAT / SSH key."
  exit 1
fi
echo "✅ Successfully pushed to remote repository!"
