package main.presentation.common;

import com.badlogic.gdx.Input;

public class GdxKeyMappings
{
	public static final String BACKSPACE = "[BACKSPACE]";
	public static final String ENTER = "[ENTER]";
	public static final String ESCAPE = "[ESCAPE]";
	
	public static int keyCode(char character)
	{
		switch (character)
		{
		case 'A':
			return Input.Keys.A;
		case 'B':
			return Input.Keys.B;
		case 'C':
			return Input.Keys.C;
		case 'D':
			return Input.Keys.D;
		case 'E':
			return Input.Keys.E;
		case 'F':
			return Input.Keys.F;
		case 'G':
			return Input.Keys.G;
		case 'H':
			return Input.Keys.H;
		case 'I':
			return Input.Keys.I;
		case 'J':
			return Input.Keys.J;
		case 'K':
			return Input.Keys.K;
		case 'L':
			return Input.Keys.L;
		case 'M':
			return Input.Keys.M;
		case 'N':
			return Input.Keys.N;
		case 'O':
			return Input.Keys.O;
		case 'P':
			return Input.Keys.P;
		case 'Q':
			return Input.Keys.Q;
		case 'R':
			return Input.Keys.R;
		case 'S':
			return Input.Keys.S;
		case 'T':
			return Input.Keys.T;
		case 'U':
			return Input.Keys.U;
		case 'V':
			return Input.Keys.V;
		case 'W':
			return Input.Keys.W;
		case 'X':
			return Input.Keys.X;
		case 'Y':
			return Input.Keys.Y;
		case 'Z':
			return Input.Keys.Z;
		case '1':
			return Input.Keys.NUM_1;
		case '2':
			return Input.Keys.NUM_2;
		case '3':
			return Input.Keys.NUM_3;
		case '4':
			return Input.Keys.NUM_4;
		case '5':
			return Input.Keys.NUM_5;
		case '6':
			return Input.Keys.NUM_6;
		case '7':
			return Input.Keys.NUM_7;
		case '8':
			return Input.Keys.NUM_8;
		case '9':
			return Input.Keys.NUM_9;
		case '0':
			return Input.Keys.NUM_0;
		default:
			return Input.Keys.UNKNOWN;
		}
	}
}
