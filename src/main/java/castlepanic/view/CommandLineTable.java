package castlepanic.view;

import java.util.List;
import java.util.ArrayList;

public class CommandLineTable
{
	private boolean _displayHeaders;
	private List<Column> _columns = new ArrayList<>();
	
	public CommandLineTable()
	{
		_displayHeaders = false;
	}
	
	public int addColumn( String header )
	{
		return addColumn( header, false );
	}
	
	public int addColumn( String header, boolean center )
	{
		_columns.add( new Column( header ) );
		_columns.get( _columns.size() - 1 ).center = center;
		return _columns.size() - 1;
	}
	
	public boolean add( String value, int row, int col )
	{
		if( col < 0 || col >= _columns.size() || row < 0 )
			return false;
		Column column = _columns.get( col );
		while( column.values.size() < row + 1 )
			column.values.add( "" );
		column.values.set( row, value );
		return true;
	}
	
	public boolean add( String value, int col )
	{
		if( col < 0 || col >= _columns.size() )
			return false;
		Column column = _columns.get( col );
		return add( value, column.values.size(), col );
	}
	
	public String format( String value, int maxWidth, boolean center )
	{
		StringBuilder sb = new StringBuilder();
		int padding = Math.max( maxWidth - value.length(), 0 );
		if( center )
		{
			for( int i = 0; i < padding / 2; ++i )
				sb.append( " " );
		}
		sb.append( value );
		if( center )
		{
			for( int i = 0; i < padding / 2; ++i )
				sb.append( " " );
		}
		while( sb.length() < maxWidth )
			sb.append( " " );
		return sb.toString();
	}
	
	public String toString()
	{
		Map<Column, Integer> colWidths = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		int valRow = 0;
		for( int tblRow = 0; tblRow < _columns.get( 0 ).values.size(); ++tblRow )
		{
			for( int col = 0; col < _columns.size(); ++col )
			{
				Column column = _columns.get( col );
				int maxWidth = 0;
				if( colWidths.containsKey( column ) )
					maxWidth = colWidths.get( column );
				else
				{
					maxWidth = column.getMaxWidth();
					colWidths.put( column, maxWidth );
				}
				
				if( tblRow == 0 && _displayHeaders )
				{
					sb.append( format( column.header, maxWidth ) );
				}
				else
				{
					sb.append( format( column.values.get( valRow ), maxWidth, column.center ) );
					++valRow;
				}
			}
		}
		return sb.toString();
	}
	
	
	private static class Column
	{
		public String header;
		public List<String> values = new ArrayList<>();
		public boolean center;
		
		public Column( String h )
		{
			this.header = h;
			this.center = false;
		}
		
		public int getMaxWidth()
		{
			int m = 0;
			for( String v: values )
			{
				if( v.length() > m )
					m = v.length();
			}
			return m;
		}
	}
}