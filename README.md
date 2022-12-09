# GRS-CS-655 Project: Password Cracker
Team members: Kaize Shi, Zhenhuan Wu

GitHub link: https://github.com/Wu-Zhenhuan/CS655Project

GENI slice name: KaizeZhenhuanProj

Demo video link: https://drive.google.com/drive/folders/1tvCm_xemiQUwYB-C3yn0RQf29UVbYPcS?usp=sharing

To run the program, you need to login to the necessary nodes. The manager and user nodes are necessary, and, for instance, if you want to user 5 workers, you need to pick 5 worker nodes and login to them. For our experiment, we used PuTTY on Windows to conduct the tests.

By default, the nodes do not have Java installed, so you need to download our GitHub repository first by using the command:

git clone https://github.com/Wu-Zhenhuan/CS655Project.git

Then you need to switch to the directory CS655Project, and use the following commands to execute the script to install Java:

chmod u+x Java.sh

./Java.sh

After successful installation, you need to go to the src directory and compile the code. Different nodes require different compiling and running commands.

On the manager node, you need the following command to compile the code:

javac Manager.java

On the user node, you need the following command to compile the code:

javac User.java

On the worker node, you need the following command to compile the code:

javac Worker.java

On the manager node, you need the following command to run the code (you have to pick the port number by yourself):

java Manager <manager_port_number>

On the user node, you need the following command to run the code:

java User <manager_host_address> <manager_port_number>

On the worker node, you need the following command to run the code:

java Worker <manager_host_address> <manager_port_number>

For the experiment, we used the following arguments to run the programs on the nodes:

java Manager 10000

java User 206.196.180.227 10000

java Worker 206.196.180.227 10000

For the program to function properly, you must run the manager first, and then you can run the user and worker(s). After you started all the programs required, you will be mainly interacting with the user node (see the screenshots below).

On the user’s terminal, you can use the following commands based on your needs:

info: check the information of the available workers, including their host addresses and port numbers

del <worker_host_address>: remove a worker by specifying its IP

crack <MD5>: submit a password’s MD5 code for the workers to crack

exit: quit the program and terminate the manager and workers

To submit a cracking job to the workers, you need to specify your plain text first, then use a tool of your choice (e.g., https://www.md5hashgenerator.com/) to obtain its MD5 hash code. Thus, you can use the crack command to submit the workload.
