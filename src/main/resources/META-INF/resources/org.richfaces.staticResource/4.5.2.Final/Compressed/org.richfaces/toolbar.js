(function(B,A){A.ui=A.ui||{};
A.ui.toolbarHandlers=function(E){if(E.id&&E.events){B(".rf-tb-itm",document.getElementById(E.id)).bind(E.events)
}var C=E.groups;
if(C&&C.length>0){var H;
var F;
for(F in C){H=C[F];
if(H){var D=H.ids;
var I;
var G=[];
for(I in D){G.push(document.getElementById(D[I]))
}B(G).bind(H.events)
}}}}
})(RichFaces.jQuery,RichFaces);