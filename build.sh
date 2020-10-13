#!/bin/sh

javac `find . -iname "*.java" | grep -v "core"`

