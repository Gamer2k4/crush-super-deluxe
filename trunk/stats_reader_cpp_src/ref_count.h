	#ifndef REF_COUNT
#define REF_COUNT

class reference_count
{
public :
	reference_count(void)				{ m_reference_count = 0L; }

protected :
	virtual ~reference_count(void)		{ }

protected :
	unsigned long int m_reference_count;

public :
	void add_reference(void)					{ m_reference_count++; }
	void release(void);
};


#endif	// REF_COUNT
