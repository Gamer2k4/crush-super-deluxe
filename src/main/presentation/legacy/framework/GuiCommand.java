package main.presentation.legacy.framework;

public class GuiCommand
{
	private final GuiCommandType type;
	private final int argument1;
	private final int argument2;
	
	public GuiCommand(GuiCommandType type)
	{
		this(type, -1, -1 );
	}
	
	public GuiCommand(GuiCommandType type, int argument1)
	{
		this(type, argument1, -1);
	}
	
	public GuiCommand(GuiCommandType type, int argument1, int argument2)
	{
		this.type = type;
		this.argument1 = argument1;
		this.argument2 = argument2;
	}

	public GuiCommandType getType()
	{
		return type;
	}

	public int getArgument1()
	{
		return argument1;
	}

	public int getArgument2()
	{
		return argument2;
	}
	
	public GuiCommand addArgument(int argument)
	{
		if (argument2 != -1)
			throw new IllegalStateException("Cannot add argument to a GUI command that already has two arguments defined.");
		if (argument1 != -1)
			return new GuiCommand(type, argument1, argument);
		return new GuiCommand(type, argument);
	}
	
	@Override
	public String toString()
	{
		return type.name() + "[" + argument1 + "," + argument2 + "]";
	}
	
	public static GuiCommand mouseMoved(int x, int y)
	{
		return new GuiCommand(GuiCommandType.MOUSE_MOVE, x, y);
	}
	
	public static GuiCommand mousePress(int x, int y)
	{
		return new GuiCommand(GuiCommandType.MOUSE_PRESS, x, y);
	}
	
	public static GuiCommand mouseDoubleClick(int x, int y)
	{
		return new GuiCommand(GuiCommandType.MOUSE_DOUBLE_CLICK, x, y);
	}
	
	public static GuiCommand mouseRelease(int x, int y)
	{
		return new GuiCommand(GuiCommandType.MOUSE_RELEASE, x, y);
	}
}
