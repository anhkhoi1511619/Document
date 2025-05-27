
public enum MachineError {
		CONTROLLER1("EC-1", 1, 1),
		CONTROLLER2("EC-2", 1, 2),
		CONTROLLER3("EC-3", 1, 3),
		READER1("ER-1", 2, 1),
		READER2("ER-2", 2, 2),
		READER3("ER-3", 2, 4),
		READER4("ER-4", 2, 5),
		READER5("ER-5", 2, 6),
		READER6("ER-6", 2, 7),
		READER7("ER-7", 2, 8),
		READER8("ER-8", 2, 9),
		READER9("ER-9", 2, 10),
		READER10("ER-10", 2, 11),
		READER11("ER-11", 2, 12),
		READER12("ER-12", 2, 13),
		READER13("ER-13", 2, 14),
		READER14("ER-14", 2, 15),
		READER15("ER-15", 2, 16),
		READER16("ER-16", 2, 17),
		SUBBOARD("ES-Subboard", 3, 1),
		FROZEN("EF-UIFrozen", 4, 1),
		STARTUP("ES-Startup", 5, 1);
    public static MachineError find(int v, int id) {
        for (MachineError x : values()) if (x.key == v && x.id == id) return x;
        return values()[0];
    }
    MachineError (String name, int key, int id)
	{
		this.name = name;
		this.key = key;
		this.id = id;
	}
	
	public String name;
	public int key;
	public int id;
}
