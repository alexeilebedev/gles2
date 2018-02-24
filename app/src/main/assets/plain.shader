// section vshader
uniform mat4 _mvpmat;
attribute vec4 vPosition;
void main() {
    gl_Position = _mvpmat * vPosition;
}

// section fshader
precision mediump float;
uniform vec4 vColor;
void main() {
    gl_FragColor = vColor;
}
