// section vshader
uniform mat4 _mvpmat;
uniform float  _seed;
varying vec3 _coord;
attribute vec4 _pos;
attribute vec4 _color;
void main() {
    gl_Position = _mvpmat * _pos;
    _coord = vec3(_pos.x,_pos.y,_pos.z);
}

// section fshader
uniform float  _seed;
varying vec3 _coord;
float rand(vec2 co) {
    return abs(sin(co.x/20.0)*cos(co.y/20.0));
    //return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}
void main() {
    gl_FragColor.r = rand(gl_FragCoord.xy);
    gl_FragColor.g = gl_FragColor.r;
    gl_FragColor.b = gl_FragColor.r;
    gl_FragColor.a = 1.0;
}
