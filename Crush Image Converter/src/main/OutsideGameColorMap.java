package main;

import java.awt.Color;

public class OutsideGameColorMap implements ColorMap
{

	@Override
	public Color getColor(int colorCode)
	{
		switch (colorCode)
		{
		case 0:
			return new Color(0, 0, 0);
		case 46:
			return new Color(240, 167, 0);
		case 52:
			return new Color(231, 143, 0);
		case 100:
			return new Color(176, 119, 0);
		case 106:
			return new Color(151, 79, 0);
		case 108:
			return new Color(231, 48, 0);
		case 112:
			return new Color(240, 40, 0);
		case 114:
			return new Color(255, 32, 0);
		case 117:
			return new Color(255, 24, 0);
		case 118:
			return new Color(248, 15, 8);
		case 119:
			return new Color(231, 8, 8);
		case 120:
			return new Color(207, 16, 8);
		case 121:
			return new Color(215, 8, 8);
		case 122:
			return new Color(200, 8, 8);
		case 123:
			return new Color(192, 8, 8);
		case 130:
			return new Color(183, 7, 7);
		case 134:
			return new Color(144, 16, 15);
		case 135:
			return new Color(152, 8, 8);
		case 136:
			return new Color(135, 8, 8);
		case 142:
			return new Color(96, 0, 96);
		case 144:
			return new Color(95, 0, 95);
		case 145:
			return new Color(88, 0, 88);
		case 148:
			return new Color(80, 0, 80);
		case 149:
			return new Color(87, 0, 87);
		case 153:
			return new Color(79, 0, 79);
		case 156:
			return new Color(72, 0, 72);
		case 159:
			return new Color(71, 0, 71);
		case 184:
			return new Color(119, 8, 8);
		case 185:
			return new Color(104, 8, 7);
		case 189:
			return new Color(72, 8, 7);
		case 199:
			return new Color(63, 0, 63);
		case 202:
			return new Color(56, 0, 56);
		case 203:
			return new Color(55, 0, 55);
		case 206:
			return new Color(48, 0, 48);
		case 210:
			return new Color(47, 0, 47);
		case 211:
			return new Color(40, 0, 40);
		case 214:
			return new Color(39, 0, 39);
		case 217:
			return new Color(32, 0, 32);
		case 225:
			return new Color(55, 7, 7);
		case 226:
			return new Color(39, 7, 8);
		case 231:
			return new Color(31, 0, 31);
		case 234:
			return new Color(24, 0, 24);
		case 236:
			return new Color(23, 0, 23);
		case 239:
			return new Color(16, 0, 16);
		case 246:
			return new Color(15, 0, 15);
		case 247:
			return new Color(8, 0, 8);
		case 251:
			return new Color(7, 0, 7);
		case 254:
			return new Color(0, 0, 0, 0);
		}

//		return Color.CYAN;

		throw new IllegalArgumentException("Unrecognized color: " + colorCode);
	}
}
