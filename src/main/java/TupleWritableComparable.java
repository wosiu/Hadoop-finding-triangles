import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.join.TupleWritable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by m on 17.04.15.
 */
public class TupleWritableComparable extends TupleWritable implements WritableComparable {

	@Override
	public int compareTo(Object o) {
		/*
		TupleWritableComparable toCompare = (TupleWritableComparable) o;
		int n = this.size();

		for ( int i = 0; i < n; i++ ) {
			if ( this.get(i) == toCompare.get(i) ) {
				continue;
			}
			return (this.get(i) < toCompare.get(i)) ? -1 : 1;
		}
		*/
		return 0;
	}
}
