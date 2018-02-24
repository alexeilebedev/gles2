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
    return 0.5 + 0.5*sin(co.x*600.0)*cos(co.y*600.0);
}
void main() {
    gl_FragColor.r = rand(_coord.xy);
    gl_FragColor.g = 0.5 + 0.5*sin(gl_PointCoord.x*4.0+_seed);
    gl_FragColor.b = 0.5 + 0.5*cos(gl_PointCoord.y*4.0+_seed);
    gl_FragColor.a = 1.0;
}
