package me.meta1203.plugins.satoshis.cryptocoins;

public interface GenericTransaction <T> {
	public T getTransaction();
	public void setTransaction(Object tx);
}
