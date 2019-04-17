package model;

import java.util.List;

// do not use incapsulation, using just as a container
public class TaggedList {

	public List<Event> list;
	public Integer tag;
	
	public TaggedList(List<Event> list, Integer tag) {
		
		this.list = list;
		this.tag = tag;
	}
}
