/*
 * Created on Sep 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.io.IOUtil;

public final class JCVIAuthorizerUtils {

   
    public static JCVIAuthorizer readPasswordFile(File passFile) throws FileNotFoundException{
        return readPasswordFile(new FileInputStream(passFile));
    }
    public static JCVIAuthorizer readPasswordFile(String pathToPassFile) throws FileNotFoundException{
        return readPasswordFile(new FileInputStream(pathToPassFile));
    }
    public static TigrAuthorizer readTigrPasswordFile(File passFile) throws FileNotFoundException{
        return readTigrPasswordFile(new FileInputStream(passFile));
    }
    public static TigrAuthorizer readTigrPasswordFile(String pathToPassFile) throws FileNotFoundException{
        return readTigrPasswordFile(new FileInputStream(pathToPassFile));
    }
    public static TigrAuthorizer readTigrPasswordFile(InputStream passFileInputStream){
        Scanner scanner=null;
        char[] password=null;
        try{
            scanner = new Scanner(passFileInputStream);
            String username =scanner.nextLine();
            String project = scanner.nextLine();
            password = scanner.nextLine().toCharArray();
            String server = scanner.nextLine();
            return new DefaultTigrAuthorizer(
                    new DefaultJCVIAuthorizer(username, password),
                    project,server);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(scanner);
            clearPassword(password);
        }
    }
    public static JCVIAuthorizer readPasswordFile(InputStream passFileInputStream){
        Scanner scanner=null;
        char[] password=null;
        try{
            scanner = new Scanner(passFileInputStream);
            String username =scanner.nextLine();            
            password = scanner.nextLine().toCharArray();
            return new DefaultJCVIAuthorizer(username, password);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(scanner);
            clearPassword(password);
        }
    }
    private static  void clearPassword(char[] password){
        Arrays.fill(password, ' ');
    }
    public static JCVIAuthorizer promptPassword(Console console, String username){
        char[] password = console.readPassword("%s's password: ", username);
        try{
            return new DefaultJCVIAuthorizer(username, password);
        }
        finally{
            clearPassword(password);
        }
    }
    /**
     * Add common Project DB login options including:
     * <ul>
     * <li> {@code -S} to specify the Project Server</li>
     * <li> {@code -D} to specify which Project database to log into</li>
     * <li> {@code -U} to specify which user name to log in as</li>
     * <li> {@code -P} to specify username's password (not recommended to use)</li>
     * <li> {@code -p} to specify login credentals using a Project DB password file</li>
     * </ul>
     * If a user uses the -U option without providing a password, the console
     * will prompt for a password. (recommended)
     * @param options the {@link Options} instance to add the login options to.
     * @param isDatabaseRequired forces the {@code -D} option to be required.
     */
    public static void addProjectDbLoginOptionsTo(Options options, boolean isDatabaseRequired) {
        options.addOption(new CommandLineOptionBuilder("S","server","name of server")
                                .longName("Server")
                                .build());
        options.addOption(new CommandLineOptionBuilder("D","database","name of server")
                                .isRequired(isDatabaseRequired)
                                .build());
       
        options.addOption(new CommandLineOptionBuilder("p","passfile","password file")
                    .longName("passfile")
                            .build());
        options.addOption(new CommandLineOptionBuilder("U","username","name of user")                            
                            .longName("username")                    
                            .build());
        options.addOption(new CommandLineOptionBuilder("P","password","password of user")                            
                            .longName("password")                    
                            .build());
    }
    /**
     * Reads Project DB Login Options created by {@link #addProjectDbLoginOptionsTo(Options, boolean)}
     * @param commandLine the {@link CommandLine} instance to read.
     * @param console the {@link Console} instance to use if password prompting is needed.
     * @return an instance of {@link TigrAuthorizer} using the Project Db login credentials.
     * @throws FileNotFoundException if the specified Password file is not found.
     */
    public static List<TigrAuthorizer> getMultipleProjectDbAuthorizersFrom(
            CommandLine commandLine, final Console console)
            throws FileNotFoundException {
        List<TigrAuthorizer> authorizers = new ArrayList<TigrAuthorizer>();
        if(!commandLine.hasOption("D")){
            throw new IllegalArgumentException("Database(s) must be set!");
        }
        DefaultTigrAuthorizer.Builder masterAuthorizer = new DefaultTigrAuthorizer.Builder();
        if(commandLine.hasOption("p")){
            masterAuthorizer.authorizer(JCVIAuthorizerUtils.readTigrPasswordFile(commandLine.getOptionValue("p")));
        }
        if(commandLine.hasOption("U")){
            JCVIAuthorizer auth = parseAuthorizerFrom(commandLine, console);
            masterAuthorizer.authorizer(auth);
        }
        if(commandLine.hasOption("S")){
            masterAuthorizer.server(commandLine.getOptionValue("S"));
        }
        String dbs = commandLine.getOptionValue("D");
        for(String db : dbs.split(",")){
            DefaultTigrAuthorizer.Builder builder = new DefaultTigrAuthorizer.Builder(masterAuthorizer);
            builder.project(db); 
            authorizers.add(builder.build());
        }
       
        return authorizers;
    }
    /**
     * Reads Project DB Login Options created by {@link #addProjectDbLoginOptionsTo(Options, boolean)}
     * @param commandLine the {@link CommandLine} instance to read.
     * @param console the {@link Console} instance to use if password prompting is needed.
     * @return an instance of {@link TigrAuthorizer} using the Project Db login credentials.
     * @throws FileNotFoundException if the specified Password file is not found.
     */
    public static TigrAuthorizer getProjectDbAuthorizerFrom(
            CommandLine commandLine, final Console console)
            throws FileNotFoundException {
        DefaultTigrAuthorizer.Builder tigrAuthBuilder = new DefaultTigrAuthorizer.Builder();
        if(commandLine.hasOption("p")){
            tigrAuthBuilder.authorizer(JCVIAuthorizerUtils.readTigrPasswordFile(commandLine.getOptionValue("p")));
        }
        if(commandLine.hasOption("U")){
            JCVIAuthorizer auth = parseAuthorizerFrom(commandLine, console);
            tigrAuthBuilder.authorizer(auth);
        }
        if(commandLine.hasOption("S")){
            tigrAuthBuilder.server(commandLine.getOptionValue("S"));
        }
        if(commandLine.hasOption("D")){
            tigrAuthBuilder.project(commandLine.getOptionValue("D"));
        }
   
        TigrAuthorizer authorizer = tigrAuthBuilder.build();
        return authorizer;
    }
    /**
     * Parse a username and password from the commandline using the 
     * -U option for username and either -P for password or prompt
     * the console for user input.
     * @param commandLine the {@link CommandLine} instance to read.
     * @param console the {@link Console} instance to use if password prompting is needed.
     * @return an instance of {@link JCVIAuthorizer} using the login credentials.
     * @throws FileNotFoundException if the specified Password file is not found.
     */
    public static JCVIAuthorizer parseAuthorizerFrom(CommandLine commandLine,
            final Console console) {
        String username = commandLine.getOptionValue("U");
        JCVIAuthorizer auth;
        if(commandLine.hasOption("P")){
            auth = new DefaultJCVIAuthorizer(username, commandLine.getOptionValue("P").toCharArray());
        }
        else{
            auth = JCVIAuthorizerUtils.promptPassword(console, username);
        }
        return auth;
    }
    
}
