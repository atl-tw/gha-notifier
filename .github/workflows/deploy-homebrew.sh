#!/bin/bash
set -x
set -e
cd target
CLONE_DIRECTORY=homebrew

git config --global user.email "$COMMIT_EMAIL"
git config --global user.name "$COMMIT_USERNAME"

git clone --single-branch --branch "$DESTINATION_BRANCH" "https://$API_TOKEN_GITHUB@github.com/$DESTINATION_REPOSITORY.git" "$CLONE_DIRECTORY"
ls -la "$CLONE_DIRECTORY"

mkdir -p "$CLONE_DIRECTORY/$DESTINATION_DIRECTORY"
cd ..

cp -rvf $SOURCE_FILES "target/$CLONE_DIRECTORY/$DESTINATION_DIRECTORY"

cd target/$CLONE_DIRECTORY
git add .
git status

git diff-index --quiet HEAD || git commit --message "$COMMIT_MESSAGE"
git config --unset-all "http.https://github.com/.extraheader" || echo "Failed"
git push origin --set-upstream "$DESTINATION_BRANCH"