# GRS-CS-655 Project: Password Cracker
<strong>Team members: Kaize Shi, Zhenhuan Wu</strong>

* GitHub link: https://github.com/Wu-Zhenhuan/CS655Project

* GENI slice name: KaizeZhenhuanProj

* GENI configuration: https://github.com/Wu-Zhenhuan/CS655Project/blob/main/KaizeZhenhuanProj_request_rspec.xml

* Demo video link: https://drive.google.com/drive/folders/1tvCm_xemiQUwYB-C3yn0RQf29UVbYPcS?usp=sharing

To run the program, you need to login to the necessary nodes. The manager and user nodes are necessary, and, for instance, if you want to user 5 workers, you need to pick 5 worker nodes and login to them. For our experiment, we used PuTTY on Windows to conduct the tests.

By default, the nodes do not have Java installed, so you need to download our GitHub repository first by using the command:

<code>git clone https://github.com/Wu-Zhenhuan/CS655Project.git</code>

Then you need to switch to the directory CS655Project, and use the following commands to execute the script to install Java:

<code>chmod u+x Java.sh</code>

<code>./Java.sh</code>

After successful installation, you need to go to the src directory and compile the code. Different nodes require different compiling and running commands.

On the manager node, you need the following command to compile the code:

<code>javac Manager.java</code>

On the user node, you need the following command to compile the code:

<code>javac User.java</code>

On the worker node, you need the following command to compile the code:

<code>javac Worker.java</code>

On the manager node, you need the following command to run the code (you have to pick the port number by yourself):

<code>java Manager <manager_port_number></code>

On the user node, you need the following command to run the code:

<code>java User <manager_host_address> <manager_port_number></code>

On the worker node, you need the following command to run the code:

<code>java Worker <manager_host_address> <manager_port_number></code>

For the experiment, we used the following arguments to run the programs on the nodes:

<code>java Manager 10000</code>

<code>java User 206.196.180.227 10000</code>

<code>java Worker 206.196.180.227 10000</code>

For the program to function properly, you must run the manager first, and then you can run the user and worker(s). After you started all the programs required, you will be mainly interacting with the user node (see the screenshots below).

On the user’s terminal, you can use the following commands based on your needs:

<code>info</code>: check the information of the available workers, including their host addresses and port numbers

<code>del <worker_host_address></code>: remove a worker by specifying its IP

<code>crack \<MD5\></code>: submit a password’s MD5 code for the workers to crack

<code>exit</code>: quit the program and terminate the manager and workers

To submit a cracking job to the workers, you need to specify your plain text first, then use a tool of your choice (e.g., https://www.md5hashgenerator.com/) to obtain its MD5 hash code. Thus, you can use the crack command to submit the workload.
