#!/bin/sh

# gamedir from lutris
GAMEDIR="Games"

# fully qualified java path
java="/usr/lib/jvm/temurin-11-jdk/bin/java"

# fully qualified lutris install path
path="${HOME}/${GAMEDIR}/jagex-launcher/drive_c/Program Files (x86)/Jagex Launcher/Games/RuneLite"

# runelite hijack
class="${path}/RuneliteHijack.jar:${path}/RuneLite.jar"
main="ca.arnah.runelite.LauncherHijack"

# for debug
echo ${path}
echo ${class}
echo ${main}

"${java}" -cp "${class}" $@ "${main}"
