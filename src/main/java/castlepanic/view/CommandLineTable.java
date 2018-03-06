package castlepanic.view;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class CommandLineTable
{
	private boolean _displayHeaders;
	private boolean _displayRowNames;
	private List<Column> _columns = new ArrayList<>();
    private Column _rowNames = new Column();
    private String _columnSeparator;
	
	public CommandLineTable()
	{
		_displayHeaders = false;
		_displayRowNames = false;
        _columnSeparator = "";
	}
	
	public int addColumn( String header )
	{
		return addColumn( header, false );
	}
	
	public int addColumn( String header, boolean center )
	{
		_columns.add( new Column( header, center ) );
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
        if( value == null )
            return "null";
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

        List<Column> dispColumns = new ArrayList<>();
        if( _displayRowNames )
        {
            while( _rowNames.values.size() < _columns.get( 0 ).values.size() )
                _rowNames.values.add( "" );
            dispColumns.add( _rowNames );
        }
        dispColumns.addAll( _columns );

        if( _displayHeaders )
        {
            for( int col = 0; col < dispColumns.size(); ++col )
            {
                Column column = dispColumns.get( col );
                int maxWidth = 0;
                if( colWidths.containsKey( column ) )
                    maxWidth = colWidths.get( column );
                else
                {
                    maxWidth = column.getMaxWidth();
                    colWidths.put( column, maxWidth );
                }

                sb.append( format( column.header == null? "": column.header, maxWidth, true ) );
                sb.append( _columnSeparator );
            }
            sb.append( "\n" );
        }

		for( int row = 0; row < dispColumns.get( 0 ).values.size(); ++row )
		{
			for( int col = 0; col < dispColumns.size(); ++col )
			{
				Column column = dispColumns.get( col );
				int maxWidth = 0;
				if( colWidths.containsKey( column ) )
					maxWidth = colWidths.get( column );
				else
				{
					maxWidth = column.getMaxWidth();
					colWidths.put( column, maxWidth );
				}
				
                String val = (column.values.size() > row && column.values.get( row ) != null)? column.values.get( row ): "";
                sb.append( format( val, maxWidth, column.center ) );
                sb.append( _columnSeparator );
			}
            sb.append( "\n" );
		}
		return sb.toString();
	}

	public boolean shouldDisplayHeaders(){ return _displayHeaders; }
	public void setDisplayHeaders( boolean v ){ _displayHeaders = v; }

	public boolean shouldDisplayRowNames(){ return _displayRowNames; }
	public void setDisplayRowNames( boolean v ){ _displayRowNames = v; }
	
    public List<String> getRowNames(){ return _rowNames.values; }
    public void setRowNames( List<String> names ){ _rowNames.values = names; }

    public void setRowNamesHeader( String h ){ _rowNames.header = h; }

    public String getColumnSeparator(){ return _columnSeparator; }
    public void setColumnSeparator( String p ){ _columnSeparator = p; }
	
	private static class Column
	{
		public String header;
		public List<String> values = new ArrayList<>();
		public boolean center;

        public Column()
        {
            this( null );
        }

		public Column( String h )
		{
			this( h, false );
		}
		
		public Column( String h, boolean c )
		{
			this.header = h;
			this.center = c;
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
