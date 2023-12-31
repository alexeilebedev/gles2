// section vshader

uniform mat4 _mvpmat;
attribute vec4 vPosition;
varying vec3 vCoord;
void main() {
    gl_Position = _mvpmat * vPosition;
    vCoord = vec3(vPosition.x,vPosition.y,vPosition.z);
}


// section fshader

precision highp float;
uniform int _maxiter;
varying vec3 vCoord;

// c: h,s,v
vec3 hsv2rgb(vec3 c) {
  vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
  vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
  return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
	vec2 p = vec2(vCoord.x,vCoord.y);
	vec2 c = p;
	vec3 color = vec3(0.0,0.0,0.0);
	for(int i=0; i < _maxiter; i++){
		p= vec2(p.x*p.x-p.y*p.y,2.0*p.x*p.y)+c;
		if (dot(p,p)>4.0){
			float f = clamp(1.f - float(i-_maxiter/4)/float(_maxiter), 0.f, 1.f);
			color = vec3(f,f,f);
			break;
		}
	}
	gl_FragColor.rgb = color;
	gl_FragColor.a = 1.0;
}
