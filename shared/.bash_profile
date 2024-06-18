# .bash_profile

# Get the aliases and functions
if [ -f ~/.bashrc ]; then
    . ~/.bashrc
fi

# User specific environment and startup programs
# copy over most recent plugins
cp mnt/build/libs/*jar 				~/.runelite/externalplugins/
cp mnt/extern/PiggyPlugins/build/libs/*jar 	~/.runelite/externalplugins/
