(
MIDIClient.init;
MIDIClient.destinations;
MIDIOut.connect(1, MIDIClient.destinations[1].uid);

m = MIDIOut(1);
)



/////////////////////
// adr
 /////////////////////
(
NetAddr("localhost", 49160).sendMsg("select:0;0;1;1");
NetAddr("localhost", 49160).sendMsg("inject:siren0;0;0");
NetAddr("localhost", 49160).sendMsg("bpm:135");
)
/////////////////////
// afd
 /////////////////////
(
NetAddr("localhost", 49160).sendMsg("select:0;0;1;1");
NetAddr("localhost", 49160).sendMsg("inject:siren1;0,0");
NetAddr("localhost", 49160).sendMsg("bpm:140");
m.program(4, 23); // preset 7
)



/////////////////////
// path
 /////////////////////
(
NetAddr("localhost", 49160).sendMsg("select:0;0;1;1");
NetAddr("localhost", 49160).sendMsg("inject:siren3;0;0");
NetAddr("localhost", 49160).sendMsg("bpm:144");
m.program(4, 3); // preset 4
)

m.program(4, 5);
m.program(4, 3);


/////////////////////
// pre
 /////////////////////
(
NetAddr("localhost", 49160).sendMsg("bpm:123");
m.program(4, 5); // preset 6
)




MIDIClient.disposeClient;

Scale

