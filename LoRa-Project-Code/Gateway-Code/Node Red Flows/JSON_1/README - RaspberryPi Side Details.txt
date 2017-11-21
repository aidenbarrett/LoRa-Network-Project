Raspberry Pi Details :

Steps :

[SSH into the Raspberry Pi to open a terminal (terminal-A)]
$mosquitto
[Let terminal-A run]

[SSH into the Raspberry Pi to open another terminal (terminal-B)]
$node-red start
[Wait for 20 seconds]
[Let terminal-B run]

[SSH into the Raspberry Pi to open another terminal (terminal-C)]
$tightvncserver
[Open Raspberry Pi on VNC Viewer]
[Open a browser]
[Go the the address localhost:1880]
[Node-Red will open, click menu on the top-right]
[Go to Menu > Import > Clipboard]
[Paste the contents of Flow 1.json]
[Click Import, press Enter]
[Click 'Deploy']

[SSH into the Raspberry Pi to open a terminal (terminal-D)]
[Run the python script]