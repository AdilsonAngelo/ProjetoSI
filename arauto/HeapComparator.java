package arauto;

import java.util.Comparator;

public class HeapComparator implements Comparator<Node>
	{
		@Override
		public int compare(Node a, Node b)
		{
			if (a.f < b.f)
			{
				return -1;
			}
			if (a.f > b.f)
			{
				return 1;
			}
			return 0;
		}
	}