#ifndef SMART_POINTER
#define SMART_POINTER

#pragma warning(disable:4786)

#include "ref_count.h"

#define NULL 0

///////////////////////////////////////////////////////////////////////////////
// Encapsulates a reference counted pointer (derived from reference_count)
// with automatic reference counting
// This is the base class. Use smart_pointer<T>
class smart_pointer_base
{
protected :
	smart_pointer_base(void)						{ m_pointer = NULL; }
	virtual ~smart_pointer_base(void)				{ release_pointer(); }
	smart_pointer_base (reference_count * pc_pointer)	{ m_pointer = NULL; store_pointer(pc_pointer); }

private :
	void release_pointer(void);
	void store_pointer (reference_count * pc_pointer);

private :
	reference_count * m_pointer;			// the reference counted pointer

///////////////////////////////////////////////////////////////////////////////
// Assignment
protected :
	void copy(reference_count * pc_pointer);

///////////////////////////////////////////////////////////////////////////////
// Access
protected :
	bool is_valid (void) const
	{ 
		return ( m_pointer == NULL ) ? ( false ) : ( true ); 
	}

	reference_count * get_pointer (void) const			{ return m_pointer; }
	reference_count * get_good_pointer (void) const;
	reference_count ** get_address (void);
};


///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////


template <class T> class smart_pointer
	: public smart_pointer_base
{
public :
	smart_pointer (void)							{ }
	virtual ~smart_pointer (void)					{ }
	smart_pointer (const smart_pointer<T>& rc_source)
		: smart_pointer_base (rc_source.get_pointer())		{ }

	smart_pointer(T* pc_pointer) : smart_pointer_base(pc_pointer)				{ }

///////////////////////////////////////////////////////////////////////////////
// Assignment
public :
	smart_pointer <T>& operator = (const smart_pointer<T>& rc_source)
		{ copy( rc_source.get_pointer() ); return *this; }

	smart_pointer<T>& operator = (T* pc_pointer)
		{ copy(pc_pointer); return *this; }

///////////////////////////////////////////////////////////////////////////////
// Access
public :
	bool is_valid(void) const			{ return smart_pointer_base::is_valid(); }
	operator T* (void) const			{ return (T*)get_pointer(); }
	T* operator -> (void) const			{ return (T*)get_good_pointer(); }
	//T** operator & (void)				{ return (T**)get_address(); }
};

#endif	// SMART_POINTER

