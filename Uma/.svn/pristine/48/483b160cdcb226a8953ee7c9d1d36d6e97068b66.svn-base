/*****************************************************************************\
 * Copyright (c) 2008, 2009, 2010. Dirk Fahland. AGPL3.0
 * All rights reserved. 
 * 
 * ServiceTechnology.org - Uma, an Unfolding-based Model Analyzer
 * 
 * This program and the accompanying materials are made available under
 * the terms of the GNU Affero General Public License Version 3 or later,
 * which accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/agpl.txt
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
\*****************************************************************************/

package org.processmining.plugins.uma;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

/**
 * Generic connection for two Petri nets where the second net
 * {@link #TRANSFORMED_NET} is the result of some transformation on 
 * the first net {@link #ORIGINAL_NET}.
 * 
 * @author dfahland
 */
public class NetTransformationConnection extends AbstractConnection {

  public final static String ORIGINAL_NET = "original net";
  public final static String TRANSFORMED_NET = "transformed net";

  public NetTransformationConnection(Petrinet net, Petrinet transformedNet) {
    super("Transformation result from " + net.getLabel());
    put(ORIGINAL_NET, net);
    put(TRANSFORMED_NET, transformedNet);
  }
}
