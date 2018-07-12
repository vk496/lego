LEGO
======
<p align="center">
  <img src="https://www.ekito.fr/people/wp-content/uploads/2015/11/DockerCon-Whale-Lego.jpg">
</p>

## What is LEGO?
LEGO is a project of the Faculty of Computer Science (Technology of Information ) at University of Murcia. The goal is to share between different subjects the same development scenario. Specifically, at my year there was these 3 inside the project:

- Wireless Communications Technologies
- Advanced Telematic Services
- Security

The first two was coursed together the first quarter. Security was coursed the last quarter.

The goal is to deploy two enterprise organizations with their own services (FreeRADIUS, OpenLDAP, Asterisk, OwnCloud, Firewall, etc.)

<img src="diagrama.svg"/>

## How is organized?
The project was developed cronologically. Each project delivery of the subjects will be in a different branch. Those branches will contain a **FULL technical documentation**, as PDF in Spanish (sorry :P).


## How can I test it?
Because of the time, this project have some limitations. They are:

* *Linux*. I use some iptables tricks, so right now is not possible to deploy it on a Windows or Mac. Not so difficult to fix it ;)
* *Docker*. Obiously :P

The process is very simple:


```bash
$ git clone -b sta_tci https://github.com/vk496/lego
$ cd lego
$ ./up.sh -b
```

## Disclimer
It is totally forbidden to use this project to pass subjects. The motivation of publishing this project is for learning purposes
