#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include "smart_pointer.h"
	
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

void smart_pointer_base::release_pointer(void)
{
	if ( m_pointer != NULL )
		m_pointer->release();

	m_pointer = NULL;
}

void smart_pointer_base::store_pointer(reference_count * pc_pointer)
{
	release_pointer();				// release old one

	// store new one
	m_pointer = pc_pointer;
	if ( m_pointer != NULL )
	{
		m_pointer->add_reference();
	}
}

void smart_pointer_base::copy(reference_count * pc_pointer)
{
	if ( m_pointer == pc_pointer )
	{
		return;			// prevent self-copying
	}

	store_pointer (pc_pointer);
	return;
}

reference_count * smart_pointer_base::get_good_pointer(void) const
{
	assert( m_pointer != NULL );

	return m_pointer;
}

reference_count ** smart_pointer_base::get_address(void)
{
	release_pointer();				// release old one

	return &m_pointer;
}


