# Audio Streaming using Socket API 
 Streaming Audio using Socket API in local network
![image](https://user-images.githubusercontent.com/58328457/140495262-a752447a-ab64-4b1c-9c9c-5162d1db8278.png)
The server sends bytes of audio portionally, when the portion of data is sent it block waits until the receiver sends an acknoledgement
The receiver receives the data in order, sends acknoledgment and plays the audio in real time.
The program was tested on 2 PCs in the same local network
