package main;

import java.awt.Color;

public class GreenArenaColorMap implements ColorMap
{
	@Override
	public Color getColor(int colorCode)
	{
		switch (colorCode)
		{
		case 254: // transparent
			return new Color(0, 0, 0, 0);
		case 255: // black (transparent on stats2.dve?)
			return new Color(0, 0, 0);
		case 0:
			return new Color(0, 0, 31);
		case 1:
			return new Color(0, 0, 56);
		case 2:
			return new Color(0, 0, 87);
		case 3:
			return new Color(0, 0, 112);
		case 4:
			return new Color(0, 0, 143);
		case 5:
			return new Color(0, 0, 168);
		case 6:
			return new Color(0, 0, 199);
		case 7:
			return new Color(0, 0, 224);
		case 18:
			return new Color(87, 39, 0);
		case 19:
			return new Color(112, 48, 0);
		case 20:
			return new Color(143, 63, 0);
		case 21:
			return new Color(168, 72, 0);
		case 22:
			return new Color(199, 87, 0);
		case 23:
			return new Color(224, 96, 0);
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
		case 152:
			return new Color(0, 0, 7);
		case 176:
			return new Color(0, 8, 8);
		case 177:
			return new Color(0, 16, 16);
		case 178:
			return new Color(0, 24, 24);
		case 179:
			return new Color(0, 32, 32);
		case 180:
			return new Color(0, 40, 40);
		case 181:
			return new Color(0, 48, 48);
		case 182:
			return new Color(0, 56, 56);
		case 183:
			return new Color(0, 64, 64);
		case 184:
			return new Color(0, 72, 72);
		case 185:
			return new Color(0, 80, 80);
		case 186:
			return new Color(0, 88, 88);
		case 187:
			return new Color(0, 96, 104);
		case 188:
			return new Color(0, 104, 104);
		case 189:
			return new Color(0, 112, 112);
		case 190:
			return new Color(0, 120, 120);
		case 191:
			return new Color(0, 128, 128);
		case 192:
			return new Color(0, 136, 136);
		case 193:
			return new Color(0, 143, 143);
		case 194:
			return new Color(0, 152, 152);
		case 195:
			return new Color(0, 160, 160);
		case 196:
			return new Color(0, 168, 168);
		case 197:
			return new Color(0, 176, 176);
		case 198:
			return new Color(0, 184, 184);
		case 199:
			return new Color(0, 192, 192);
		case 200:
			return new Color(0, 200, 200);
		case 201:
			return new Color(0, 208, 208);
		case 202:
			return new Color(0, 216, 216);
		case 203:
			return new Color(0, 232, 224);
		case 204:
			return new Color(0, 232, 232);
		case 205:
			return new Color(0, 240, 248);
		case 206:
			return new Color(0, 248, 248);
		}

//		return Color.GREEN;

		throw new IllegalArgumentException("Unrecognized color: " + colorCode);
	}
}
