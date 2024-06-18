#!/bin/sh

# Using system sh (not bash!), so may work on BSDs -- not tested! YMMV
# Might work for MacOS if interpreted with zsh

num="1.2.6"

# Find dir. The unoptimal way, but will have maximum coverage.

# Keep pre-setup config.json in the root of the repo.
CONFIGJSON="config.json"
if [ ! -f "$CONFIGJSON" ]; then
    echo "critical: Pre-setup '$CONFIGJSON' does not exist!"
    exit 1
fi

# Find dir containing "RuneLite.jar" and copy pre-setup config.json into them.
find . -type f -name "RuneLite.jar" -execdir cp "$CONFIGJSON" . \;

# wget latest release into the repo.
wget -O "piggy-plugins-aio-$num.jar" "https://github.com/0Hutch/PiggyPlugins/releases/download/release/piggy-plugins-aio-$num.jar"

conf=$(find "$HOME" -name .runelite -type d)
if [ -z "$conf" ]; then
    if ! mkdir "$HOME/.runelite"; then
        echo "critical: Unable to create directory '$HOME/.runelite'!"
        exit 1
    fi
    conf="$HOME/.runelite"
fi

mv "piggy-plugins-aio-$num.jar" "$conf/"
