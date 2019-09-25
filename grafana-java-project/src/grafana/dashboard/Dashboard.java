package grafana.dashboard;

import grafana.Configurable;
import grafana.datasource.DataSource;

public abstract class Dashboard implements Configurable{
	private String name;
	protected DataSource dataSource;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
