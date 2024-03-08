package main.presentation.common.image;

import java.awt.Color;

public class InGameColorMap implements ColorMap
{
	@Override
	public Color getColor(int colorCode)
	{
		switch (colorCode)
		{
		case 255: // black (transparent on stats2.dve?)
			return new Color(0, 0, 0);
		case 254: // transparent?
		case 0:	  // transparent
			return new Color(0, 0, 0, 0);
		case 1:
			return new Color(56, 0, 0);
		case 2:
			return new Color(87, 0, 0);
		case 3:
			return new Color(112, 0, 0);
		case 4:
			return new Color(143, 0, 0);
		case 5:
			return new Color(168, 0, 0);
		case 6:
			return new Color(199, 0, 0);
		case 7:
			return new Color(224, 0, 0);
		case 8:
			return new Color(31, 16, 0);
		case 9: // bg 1
			return new Color(56, 24, 0);
		case 10:
			return new Color(87, 39, 0);
		case 11:
			return new Color(112, 48, 0);
		case 12: // bg 3
			return new Color(143, 63, 0);
		case 13: // bg 4
			return new Color(168, 72, 0);
		case 14: // bg 5
			return new Color(199, 87, 0);
		case 15: // bg 6
			return new Color(224, 96, 0);
		case 16:
			return new Color(31, 23, 0);
		case 17:
			return new Color(56, 40, 0);
		case 18:
			return new Color(87, 63, 0);
		case 19:
			return new Color(112, 80, 0);
		case 20:
			return new Color(143, 103, 0);
		case 21:
			return new Color(168, 120, 0);
		case 22:
			return new Color(199, 143, 0);
		case 23:
			return new Color(224, 160, 0);
		case 24:
			return new Color(31, 31, 0);
		case 25:
			return new Color(56, 56, 0);
		case 26:
			return new Color(87, 87, 0);
		case 27:
			return new Color(112, 112, 0);
		case 28:
			return new Color(143, 143, 0);
		case 29:
			return new Color(168, 168, 0);
		case 30:
			return new Color(199, 199, 0);
		case 31:
			return new Color(224, 224, 0);
		case 32:
			return new Color(16, 31, 0);
		case 33:
			return new Color(32, 56, 0);
		case 34:
			return new Color(48, 87, 0);
		case 35:
			return new Color(64, 112, 0);
		case 36:
			return new Color(80, 143, 0);
		case 37:
			return new Color(96, 168, 0);
		case 38:
			return new Color(112, 199, 0);
		case 39:
			return new Color(128, 224, 0);
		case 40:
			return new Color(0, 31, 0);
		case 41:
			return new Color(0, 56, 0);
		case 42:
			return new Color(0, 87, 0);
		case 43:
			return new Color(0, 112, 0);
		case 44:
			return new Color(0, 143, 0);
		case 45:
			return new Color(0, 168, 0);
		case 46:
			return new Color(0, 199, 0);
		case 47:
			return new Color(0, 224, 0);
		case 48:
			return new Color(0, 31, 16);
		case 49:
			return new Color(0, 56, 32);
		case 50:
			return new Color(0, 87, 48);
		case 51:
			return new Color(0, 112, 64);
		case 52:
			return new Color(0, 143, 80);
		case 53:
			return new Color(0, 168, 96);
		case 54:
			return new Color(0, 199, 112);
		case 55:
			return new Color(0, 224, 128);
		case 56:
			return new Color(0, 31, 31);
		case 57:
			return new Color(0, 56, 56);
		case 58:
			return new Color(0, 87, 87);
		case 59:
			return new Color(0, 112, 112);
		case 60:
			return new Color(0, 143, 143);
		case 61:
			return new Color(0, 168, 168);
		case 62:
			return new Color(0, 199, 199);
		case 63:
			return new Color(0, 224, 224);
		case 64:
			return new Color(0, 16, 31);
		case 65:
			return new Color(0, 32, 56);
		case 66:
			return new Color(0, 48, 87);
		case 67:
			return new Color(0, 64, 112);
		case 68:
			return new Color(0, 80, 143);
		case 69:
			return new Color(0, 96, 168);
		case 70:
			return new Color(0, 112, 199);
		case 71:
			return new Color(0, 128, 224);
		case 72: // fg 0
			return new Color(0, 0, 31);
		case 73: // fg 1
			return new Color(0, 0, 56);
		case 74: // fg 2
			return new Color(0, 0, 87);
		case 75:
			return new Color(0, 0, 112);
		case 76: // fg 3
			return new Color(0, 0, 143);
		case 77: // fg 4
			return new Color(0, 0, 168);
		case 78: // fg 5
			return new Color(0, 0, 199);
		case 79: // fg 6
			return new Color(0, 0, 224);
		case 80:
			return new Color(31, 7, 0);
		case 81:
			return new Color(56, 8, 0);
		case 82:
			return new Color(87, 15, 0);
		case 83:	// new Color (112)
		case 84:
			return new Color(143, 23, 0);
		case 85:
			return new Color(168, 24, 0);
		case 86:
			return new Color(199, 31, 0);
		case 87:
			return new Color(224, 32, 0);
		case 88:
			return new Color(23, 0, 31);
		case 89:
			return new Color(40, 0, 56);
		case 90:
			return new Color(63, 0, 87);
		case 91:
			return new Color(80, 0, 112);
		case 92:
			return new Color(103, 0, 143);
		case 93:
			return new Color(120, 0, 168);
		case 94:
			return new Color(143, 0, 199);
		case 95:
			return new Color(160, 0, 224);
		case 96:
			return new Color(31, 0, 31);
		case 97:
			return new Color(56, 0, 56);
		case 98:
			return new Color(87, 0, 87);
		case 99:
			return new Color(112, 0, 112);
		case 100:
			return new Color(143, 0, 143);
		case 101:
			return new Color(168, 0, 168);
		case 102:
			return new Color(199, 0, 199);
		case 103:
			return new Color(224, 0, 224);
		case 104:
			return new Color(0, 16, 0);
		case 105:
			return new Color(0, 32, 0);
		case 106:
			return new Color(0, 48, 0);
		case 107:
			return new Color(0, 64, 0);
		case 108:
			return new Color(0, 80, 0);
		case 109:
			return new Color(0, 96, 0);
		case 110:
			return new Color(0, 112, 0);
		case 111:
			return new Color(0, 128, 0);
		case 112:
			return new Color(31, 0, 15);
		case 113:
			return new Color(56, 0, 24);
		case 114:
			return new Color(87, 0, 39);
		case 115:
			return new Color(112, 0, 48);
		case 116:
			return new Color(143, 0, 63);
		case 117:
			return new Color(168, 0, 72);
		case 118:
			return new Color(199, 0, 87);
		case 119:
			return new Color(224, 0, 96);
		case 120:
			return new Color(31, 16, 16);
		case 121:
			return new Color(56, 32, 32);	//TODO: possibly wrong; used in map files
		case 122:
			return new Color(87, 48, 48);
		case 123:
			return new Color(112, 64, 64);
		case 124:
			return new Color(143, 80, 80);
		case 125:
			return new Color(168, 96, 96);
		case 126:
			return new Color(199, 112, 112);
		case 127:
			return new Color(224, 128, 128);
		case 128:
			break; //TODO
		case 129:
			return new Color(56, 56, 32);
		case 130:
			return new Color(87, 87, 48);
		case 131:
			return new Color(112, 112, 64);
		case 132:
			return new Color(143, 143, 80);
		case 133:
			return new Color(168, 168, 96);
		case 134:
			return new Color(199, 199, 112);
		case 135:
			return new Color(224, 224, 128);
		case 136:
			return new Color(23, 8, 8);	//TODO: possibly wrong; used in map files
		case 137:
			return new Color(40, 16, 16);
		case 138:
			return new Color(63, 24, 24);
		case 139:
			return new Color(80, 32, 32);
		case 140:
			return new Color(103, 40, 40);
		case 141:
			return new Color(128, 48, 48);
		case 142:
			return new Color(143, 56, 56);
		case 143:
			return new Color(160, 64, 64);
		case 144:
			return new Color(23, 24, 31);
		case 145:
			return new Color(40, 48, 56);
		case 146:
			return new Color(63, 72, 87);
		case 147:
			return new Color(80, 96, 112);
		case 148:
			return new Color(103, 120, 143);
		case 149:
			return new Color(120, 144, 168);
		case 150:
			return new Color(143, 168, 199);
		case 151:
			return new Color(160, 192, 224);
		case 152:
			return new Color(0, 0, 7);
		case 153:
			return new Color(0, 0, 8);
		case 154:
			return new Color(0, 0, 15);
		case 155:
			return new Color(0, 0, 16);
		case 156:
			return new Color(0, 0, 23);
		case 157:
			return new Color(0, 0, 24);
		case 158:
			return new Color(0, 0, 31);
		case 159:
			return new Color(0, 0, 32);
		case 160:
			return new Color(7, 7, 7);
		case 161:
			return new Color(8, 8, 8);
		case 162:
			return new Color(15, 15, 15);
		case 163:
			return new Color(16, 16, 16);
		case 164:
			return new Color(23, 23, 23);
		case 165:
			return new Color(24, 24, 24);
		case 166:
			return new Color(31, 31, 31);
		case 167:
			return new Color(32, 32, 32);
		case 168:
			return new Color(111, 111, 111);
		case 169:
			return new Color(120, 120, 120);
		case 170:
			return new Color(135, 135, 135);
		case 171:
			return new Color(144, 144, 144);
		case 172:
			return new Color(159, 159, 159);
		case 173:
			return new Color(168, 168, 168);
		case 174:
			return new Color(183, 183, 183);
		case 175:
			return new Color(192, 192, 192);
		case 176:
			return new Color(24, 31, 32);
		case 177:
			return new Color(31, 32, 40);
		case 178:
			return new Color(32, 39, 47);
		case 179:
			return new Color(39, 47, 48);
		case 180:
			return new Color(40, 48, 56);
		case 181:
			return new Color(47, 55, 63);
		case 182:
			return new Color(48, 56, 64);
		case 183:
			return new Color(55, 63, 71);
		case 184:
			return new Color(56, 64, 79);
		case 185:
			return new Color(63, 71, 80);
		case 186:
			return new Color(64, 72, 87);
		case 187:
			return new Color(71, 80, 88);
		case 188:
			return new Color(72, 87, 96);
		case 189:
			return new Color(79, 88, 103);
		case 190:
			return new Color(80, 95, 104);
		case 191:
			return new Color(87, 96, 112);
		case 192:
			return new Color(95, 103, 119);
		case 193:
			return new Color(96, 104, 120);
		case 194:
			return new Color(103, 112, 127);
		case 195:
			return new Color(104, 119, 135);
		case 196:
			return new Color(111, 120, 136);
		case 197:
			return new Color(112, 127, 143);
		case 198:
			return new Color(119, 128, 151);
		case 199:
			return new Color(120, 135, 152);
		case 200:
			return new Color(127, 136, 159);
		case 201:
			return new Color(128, 144, 160);
		case 202:
			return new Color(135, 151, 168);
		case 203:
			return new Color(136, 152, 175);
		case 204:
			return new Color(143, 159, 176);
		case 205:
			return new Color(144, 160, 184);
		case 206:
			return new Color(151, 167, 191);
		case 207:
			return new Color(152, 168, 192);
		case 208:
			return new Color(31, 24, 23);
		case 209:
			return new Color(56, 48, 40);
		case 210:
			return new Color(87, 72, 63);
		case 211:
			return new Color(112, 96, 80);
		case 212:
			return new Color(143, 120, 103);
		case 213:
			return new Color(168, 144, 120);
		case 214:
			return new Color(199, 168, 143);
		case 215:
			return new Color(224, 192, 160);
		case 216:
			break;	//TODO
		case 218:
			return new Color(87, 63, 39);
		case 219:
			return new Color(112, 80, 48);
		case 220:
			return new Color(143, 103, 63);
		case 221:
			return new Color(168, 120, 72);
		case 222:
			return new Color(199, 143, 87);
		case 223:
			return new Color(224, 160, 96);
		case 224:
			return new Color(16, 15, 8);
		case 225:
			return new Color(32, 24, 16);
		case 226:
			return new Color(48, 39, 24);
		case 227:
			return new Color(64, 48, 32);
		case 228:
			return new Color(80, 63, 40);
		case 229:
			return new Color(96, 72, 48);
		case 230:
			return new Color(112, 87, 56);
		case 231:
			return new Color(128, 96, 64);
		case 232:
			return new Color(16, 8, 7);
		case 233:
			return new Color(32, 16, 8);
		case 234:
			return new Color(48, 24, 15);
		case 235:
			return new Color(64, 32, 16);
		case 236:
			return new Color(80, 40, 23);
		case 237:
			return new Color(96, 48, 24);
		case 238:
			return new Color(112, 56, 31);
		case 239:
			return new Color(128, 64, 32);
		case 240: // TODO: transparent (black for now, since it's black for the computer cursor)
			return new Color(0, 0, 0);
		case 241:
			return new Color(31, 31, 31);
		case 242:
			return new Color(56, 56, 56);
		case 243:
			return new Color(87, 87, 87);
		case 244:
			return new Color(112, 112, 112);
		case 245:
			return new Color(143, 143, 143);
		case 246:
			return new Color(168, 168, 168);
		case 247:
			return new Color(199, 199, 199);
		case 248:
			return new Color(235, 235, 235);
		case 249:
			return new Color(255, 255, 255);
		}

//		return Color.CYAN;

		throw new IllegalArgumentException("Unrecognized color: " + colorCode);
	}
}
