#!/bin/bash
echo "Manager Setup Script"
echo "Installing Java..."
sudo apt-get update
sudo apt-get --yes install default-jdk
echo "Cloning GitHub repo to current directory..."
git clone https://github.com/Wu-Zhenhuan/CS655Project.git
echo "Compiling..."
cd CS655Project
cd src
javac Manager.java
echo "Launching Manager..."
java Manager 10000
