/* @guidance: >>> ${guidances}/package-info-javadoc.md */
/**
 * Provides the Maven-plugin integration layer for Machai Ghostwriter.
 *
 * <p>This package adapts Maven goal execution to Ghostwriter's guidance and act
 * processors. It binds Maven parameters and execution context, resolves provider
 * configuration from properties files and Maven {@code settings.xml}, selects
 * the project paths to process, and supplies Maven project metadata to the
 * processor layer. The package is intentionally an integration boundary: Maven
 * lifecycle concerns remain here, while file traversal and workflow execution
 * remain in the Ghostwriter processor packages.</p>
 *
 * <h2>Goal architecture</h2>
 * <p>{@link AbstractGWMojo} contains the shared configuration and scanning
 * infrastructure. It accepts the model, path, instructions, excludes, server,
 * settings, project, session, and configuration-file inputs; loads the selected
 * properties file; optionally merges credentials and XML children from the
 * selected Maven settings server; and finally applies explicit plugin
 * parameters. Consequently, values supplied through {@code params} take
 * precedence over values loaded earlier. It also registers
 * {@code ClassFunctionalTools} when Maven supplies project context.</p>
 *
 * <p>{@link GWMojo} and {@link GWPerModuleMojo} create
 * {@code GuidanceProcessor} instances to find files containing Ghostwriter
 * guidance comments and execute the associated workflows. The aggregator goal
 * coordinates recursive project traversal itself, whereas the per-module goal
 * disables nested processor traversal and lets Maven invoke the goal for each
 * reactor module.</p>
 *
 * <p>{@link AbstractActMojo} extends the shared infrastructure for explicit
 * acts. It resolves an inline prompt or reusable act name, supports interactive
 * multi-line input, applies the optional acts location, and delegates execution
 * to {@code ActProcessor}. {@link ActMojo} exposes the aggregator
 * {@code gw:act} goal. {@link AbstractActPerModuleMojo} provides the
 * reactor-aware act behavior used by {@link ActPerModuleMojo}; that variant also
 * suppresses nested module processing because Maven schedules the individual
 * module executions.</p>
 *
 * <h2>Execution models</h2>
 * <dl>
 * <dt>{@code gw:gw} and {@code gw:act}</dt>
 * <dd>Thread-safe aggregator goals that can be invoked for a project tree and
 * do not require a {@code pom.xml}. Ghostwriter performs recursive traversal in
 * reverse project order, processing child modules before their parents. When a
 * Maven build is parallel, the aggregator processor coordinates its configured
 * worker threads.</dd>
 * <dt>{@code gw:gw-per-module} and {@code gw:act-per-module}</dt>
 * <dd>Thread-safe, non-aggregator goals that require a Maven project. Maven
 * invokes them for eligible reactor modules according to the reactor dependency
 * order, and each goal processes its current module without recursively entering
 * child modules.</dd>
 * </dl>
 *
 * <h2>Configuration</h2>
 * <p>The shared goals support the Ghostwriter properties represented by
 * {@code gw.model}, {@code gw.path}, {@code gw.instructions},
 * {@code gw.excludes}, {@code genai.serverId}, and {@code gw.config}. Act goals
 * additionally support {@code gw.act} and {@code gw.acts}. A server identifier
 * selects a matching Maven {@code settings.xml} server; its username, password,
 * and custom XML child values are copied into the processor configuration. If
 * no server identifier is supplied, the configured properties file is used, or
 * the default Ghostwriter configuration is attempted. Plugin {@code params}
 * are applied last.</p>
 *
 * <h2>Usage examples</h2>
 * <pre>{@code
 * # Scan guidance-bearing files in the main source tree.
 * mvn gw:gw -Dgw.path=src/main/java -Dgw.instructions=docs/guidance.md
 *
 * # Execute an inline act or a reusable act definition.
 * mvn gw:act -Dgw.act="Add missing public API Javadocs"
 * mvn -T 4 gw:act -Dgw.acts=acts -Dgw.act=review
 *
 * # Let Maven invoke a goal for each selected reactor module.
 * mvn gw:gw-per-module -Dgw.path=src/main/java
 * mvn gw:act-per-module -Dgw.act=review
 *
 * # Resolve provider credentials from settings.xml.
 * mvn gw:gw -Dgenai.serverId=my-ai-provider
 * }</pre>
 *
 * @see AbstractGWMojo
 * @see AbstractActMojo
 * @see GWMojo
 * @see GWPerModuleMojo
 * @see ActMojo
 * @see ActPerModuleMojo
 */
package org.machanism.machai.gw.maven;
