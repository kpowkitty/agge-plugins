## Assumptions
Knowledge of Lutris, gaming on Linux, etc. A guide for that is not the scope of this.

## Instructions
<code>git clone</code> the repo.<br><br>
Import the <code>jagex-launcher.yml</code> into Lutris.<br><br>
For <code>runelite.sh</code>, source what was provided in the repo and change as needed.<br><br>
Lutris should take care of everything for you!
#### Gotchas:
Make sure to have the correct jdk and path. The current <code>runelite.sh</code> fully qualifies Eclipse Temurin's jdk on Fedora.<br><br>
After the fact, you can find and edit the current <code>runelite.sh</code> in the Wine prefix Lutris installed.<br><br>
(Example path):
```
/Games/jagex-launcher/drive_c/Program Files (x86)/Jagex Launcher/Games/RuneLite/runelite.sh
```
(Or rework the provided <code>runelite.sh</code> before sourcing it in the Lutris install)
### Install jdk
#### Fedora:
(Taken from https://adoptium.net/installation/linux/)<br>
```
# Uncomment and change the distribution name if you are not using CentOS/RHEL/Fedora
# DISTRIBUTION_NAME=centos

cat <<EOF > /etc/yum.repos.d/adoptium.repo
[Adoptium]
name=Adoptium
baseurl=https://packages.adoptium.net/artifactory/rpm/${DISTRIBUTION_NAME:-$(. /etc/os-release; echo $ID)}/\$releasever/\$basearch
enabled=1
gpgcheck=1
gpgkey=https://packages.adoptium.net/artifactory/api/gpg/key/public
EOF
```
```
# yum install temurin-11-jdk
```
And the current <code>runelite.sh</code> java bin path will work!<br>
#### Gentoo:
```
# emerge --ask dev-java/openjdk-bin:11
```
```
$ eselect java-vm list
```
```
# eselect java-vm set system my_number
```
Java path for <code>runelite.sh</code>:
```
/usr/lib/jvm/openjdk-bin-11/bin/java
```
(Note: Gentoo's main repo bin is compiled by Eclipse Temurin)

## Help
Unless you have a wonky setup, the path for the classes should work.<br><br>
If it doesn't, run <code>lutris -d</code> with debug enabled. If the class path can't be loaded, it's a FULLY qualified path. Make sure the path matches the Lutris install directory for the Wine prefix.<br><br>
From experience, if the problem isn't with Lutris, it's from paths. Fully qualified paths will reduce errors and likely be the fix. If the class path isn't loading, find your paths and use fully qualified paths.

## Credit
Lutris yaml has been heavily borrowed from [TormStorm's jagex-launcher-linux](https://github.com/TormStorm/jagex-launcher-linux).<br><br>
I've repurposed it to work with EthanApi and PiggyPlugins.
