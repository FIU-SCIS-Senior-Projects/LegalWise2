package com.ibm.devworks.examples.java.lll;

import java.io.Serializable;

public class WebsiteTitle implements Serializable {
private String title = "Lauren's Lovely Landscapes";

public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

@Override
public String toString() {
	return "WebsiteTitle [title=" + title + "]";
}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((title == null) ? 0 : title.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	WebsiteTitle other = (WebsiteTitle) obj;
	if (title == null) {
		if (other.title != null)
			return false;
	} else if (!title.equals(other.title))
		return false;
	return true;
}

}
