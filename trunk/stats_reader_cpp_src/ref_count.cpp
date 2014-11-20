#include <stdio.h>
#include <assert.h>

#include "ref_count.h"

void reference_count::release(void)
{
	m_reference_count--;
	if ( m_reference_count <= 0 )
		delete this;

	return;
}

