/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.commonjava.maven.ext.manip.impl;

import static org.commonjava.maven.ext.manip.util.IdUtils.ga;

import java.util.Map;

import org.apache.maven.model.Model;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.commonjava.maven.ext.manip.ManipulationException;
import org.commonjava.maven.ext.manip.model.Project;
import org.commonjava.maven.ext.manip.state.BOMState;
import org.commonjava.maven.ext.manip.state.ManipulationSession;

/**
 * {@link Manipulator} implementation that can alter property sections in a project's pom file.
 * Configuration is stored in a {@link BOMState} instance, which is in turn stored in the {@link ManipulationSession}.
 */
@Component( role = Manipulator.class, hint = "property-manipulator" )
public class PropertyManipulator
    extends AlignmentManipulator
{

    @Requirement
    protected Logger logger;

    protected PropertyManipulator()
    {
    }

    public PropertyManipulator( final Logger logger )
    {
        super( logger );
        this.logger = logger;
    }

    @Override
    public void init( final ManipulationSession session )
    {
        super.init( session );
        super.baseLogger = this.logger;
    }

    @Override
    protected Map<String, String> loadRemoteBOM( BOMState state )
        throws ManipulationException
    {
        return loadRemoteOverrides( RemoteType.PROPERTY, state.getRemotePropertyMgmt() );
    }

    @Override
    protected void apply( ManipulationSession session, Project project, Model model, Map<String, String> override )
        throws ManipulationException
    {
        // Only inject the new properties at the top level.
        if ( !project.isTopPOM() )
        {
            return;
        }
        logger.info( "Applying property changes to: " + ga( project ) );

        model.getProperties()
             .putAll( override );
    }
}
