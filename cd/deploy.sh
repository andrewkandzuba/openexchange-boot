#!/usr/bin/env sh

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    mvn deploy -P sign,build-extras --settings cd/mvnsettings.xml
fi