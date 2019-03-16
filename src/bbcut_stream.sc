// s.boot

(
var outBusIndex  = ~dirt.orbits[0].synthBus.index;
var orbitGroupIndex = 2;
~cut_group = Group.after(orbitGroupIndex);
~cut_clock = ExternalClock(TempoClock(2.4)).play;
~cut_buf= BBCutBuffer.alloc(s,44100 * 2,1);
~cutter = BBCut2(
  CutGroup(
    [
      CutStream1.new(60,  ~cut_buf, dutycycle: 0.75, atkprop: 0.05, relprop: 1.0, curve:-2),
      CutMixer(6, 1.0, {0.1.rand + 0.8}, {0.8.rand - 0.4 })
    ],
    ~cut_group
  ),
  CageCut(16.0,[1,2,1,1,0.75,0.5,0.5,0.5,1,0.5] /8,{arg array; if(0.4.coin,{array.pyramid;},{array})}, {arg array; if(0.2.coin,{array.scramble;}); })
  // WarpCutProc1({[1,2,4].choose})
);
~cutter.play(~cut_clock);
)


(
~cutter.pause;
~cutter.resume;
~cut_buf.free;
~cutter.end;~cut_clock.free;~cut_group.free;
)


