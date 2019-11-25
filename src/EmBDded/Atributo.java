package EmBDded;

public class Atributo {
	public int atribid;
	public String nome;
	public String tipo;
	
	public Atributo(String nome, String tipo) {
		this.atribid = -1;
		this.nome = nome;
		this.tipo = tipo;
	}
	
	public Atributo(int id, String nome, String tipo) {
		this.atribid = id;
		this.nome = nome;
		this.tipo = tipo;
	}
}
