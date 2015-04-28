(function(C){C.hotkeys={version:"0.8",specialKeys:{8:"backspace",9:"tab",13:"return",16:"shift",17:"ctrl",18:"alt",19:"pause",20:"capslock",27:"esc",32:"space",33:"pageup",34:"pagedown",35:"end",36:"home",37:"left",38:"up",39:"right",40:"down",45:"insert",46:"del",96:"0",97:"1",98:"2",99:"3",100:"4",101:"5",102:"6",103:"7",104:"8",105:"9",106:"*",107:"+",109:"-",110:".",111:"/",112:"f1",113:"f2",114:"f3",115:"f4",116:"f5",117:"f6",118:"f7",119:"f8",120:"f9",121:"f10",122:"f11",123:"f12",144:"numlock",145:"scroll",191:"/",224:"meta"},shiftNums:{"`":"~","1":"!","2":"@","3":"#","4":"$","5":"%","6":"^","7":"&","8":"*","9":"(","0":")","-":"_","=":"+",";":": ","'":'"',",":"<",".":">","/":"?","\\":"|"}};
var A={key:"",enabledInInput:false};
function B(F){var E=(typeof F.data=="string")?{key:F.data}:F.data;
E=C.extend({},A,E);
var D=F.handler,G=E.key.toLowerCase().split(" ");
if(G.length===1&&G[0]===""){return 
}F.handler=function(H){var N=String.fromCharCode(H.which).toLowerCase(),J=(/textarea|select/i.test(H.target.nodeName)||H.target.type==="text");
if(this!==H.target&&J&&!E.enabledInInput){return 
}var O=H.type!=="keypress"&&C.hotkeys.specialKeys[H.which],P,K="",L={};
if(H.altKey&&O!=="alt"){K+="alt+"
}if(H.ctrlKey&&O!=="ctrl"){K+="ctrl+"
}if(H.metaKey&&!H.ctrlKey&&O!=="meta"){K+="meta+"
}if(H.shiftKey&&O!=="shift"){K+="shift+"
}if(O){L[K+O]=true
}else{L[K+N]=true;
L[K+C.hotkeys.shiftNums[N]]=true;
if(K==="shift+"){L[C.hotkeys.shiftNums[N]]=true
}}for(var M=0,I=G.length;
M<I;
M++){if(L[G[M]]){return D.apply(this,arguments)
}}}
}C.each(["keydown","keyup","keypress"],function(){C.event.special[this]={add:B}
})
})(jQuery);