#!/bin/bash
echo "Worker Setup Script"
echo "Installing Java..."
sudo apt-get update
sudo apt-get --yes install default-jdk
echo "Cloning GitHub repo to current directory..."
git clone https://github.com/Wu-Zhenhuan/CS655Project.git
echo "Compiling..."
cd CS655Project
cd src
javac Worker.java
echo "Launching Worker..."
java Worker 127.0.0.1 31415
