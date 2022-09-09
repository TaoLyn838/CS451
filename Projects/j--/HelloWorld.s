# ./HelloWorld.s
# Source file: HelloWorld.java
# Compiled: Thu Sep 08 16:29:13 EDT 2022

.text

main:
    subu    $sp,$sp,24 	 # Stack frame is 24 bytes long
    sw      $ra,20($sp) 	 # Save return address
    sw      $fp,16($sp) 	 # Save frame pointer
    sw      $t0,12($sp) 	 # Save register $t0
    sw      $t1,8($sp) 	 # Save register $t1
    sw      $t2,4($sp) 	 # Save register $t2
    sw      $t3,0($sp) 	 # Save register $t3
    addiu   $fp,$sp,20 	 # Save frame pointer

main.0:

main.1:
    la $t0,Constant..String0+12
    move $t1,$a0
    move $a0,$t0
    jal jminusminus.SPIM.printString
    move $a0,$t1
    la $t2,Constant..String1+12
    move $t3,$a0
    move $a0,$t2
    jal jminusminus.SPIM.printString
    move $a0,$t3
    j main.restore

main.restore:
    lw      $ra,20($sp) 	 # Restore return address
    lw      $fp,16($sp) 	 # Restore frame pointer
    lw      $t0,12($sp) 	 # Restore register $t0
    lw      $t1,8($sp) 	 # Restore register $t1
    lw      $t2,4($sp) 	 # Restore register $t2
    lw      $t3,0($sp) 	 # Restore register $t3
    addiu   $sp,$sp,24 	 # Pop stack
    jr      $ra 	 # Return to caller

.data

Constant..String0:
    .word 2 # Tag 2 indicates a string
    .word 28 # Size of object in bytes
    .word 12 # String length (not including null terminator)
    .asciiz "Hello, World" # String terminated by null character 0
    .align 3 # Next object is on a word boundary
Constant..String1:
    .word 2 # Tag 2 indicates a string
    .word 16 # Size of object in bytes
    .word 1 # String length (not including null terminator)
    .asciiz "
" # String terminated by null character 0
    .align 2 # Next object is on a word boundary


# SPIM Runtime

# Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
#
# The SPIM runtime file.

.text

# Prints the integer value passed as parameter.
jminusminus.SPIM.printInt:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,3      # Set up frame pointer

    li $v0,1            # Syscall code to print an integer
    syscall             # Prints the arg value

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # Restore the stack pointer
    jr $ra              # Return to caller

# Prints the float value passed as parameter.
jminusminus.SPIM.printFloat:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,32     # Set up frame pointer

    li $v0,2            # Syscall code to print a float
    syscall             # Prints the arg value

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # Restore the stack pointer
    jr $ra              # Return to caller

# Print the double value passed as parameter.
jminusminus.SPIM.printDouble:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,32     # Set up frame pointer

    li $v0,3            # Syscall code to print a double
    syscall             # Prints the arg value

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # restore the stack pointer
    jr $ra              # Return to caller

# Print the string value passed as parameter.
jminusminus.SPIM.printString:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,32     # Set up frame pointer

    li $v0,4            # Syscall code to print a string
    syscall             # Print the arg value

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # Restore the stack pointer
    jr $ra              # Return to caller

# Print the char value passed as parameter.
jminusminus.SPIM.printChar:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,32     # Set up frame pointer

    li $v0,11           # Syscall code to print a char
    syscall             # Print the arg value

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # Restore the stack pointer
    jr $ra              # Return to caller

# Read the integer value from the user through console.
jminusminus.SPIM.readInt:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,32     # Set up frame pointer

    li $v0,5            # Syscall code to read an integer
    syscall             # Load the integer value read from console into $v0

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # Restore the stack pointer
    jr $ra              # Return to caller

# Read the float value from the user through console.
jminusminus.SPIM.readFloat:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,32     # Set up frame pointer

    li $v0,6            # Syscall code to read a float
    syscall             # Load the float value read from console into $v0

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # Restore the stack pointer
    jr $ra              # Return to caller

# Read the double value from the user through console.
jminusminus.SPIM.readDouble:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,32     # Set up frame pointer

    li $v0,7            # Syscall code to read a double
    syscall             # Load the double value read from console into $v0

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # Restore the stack pointer
    jr $ra              # Return to caller

# Read the string value from the user through console.
jminusminus.SPIM.readString:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,32     # Set up frame pointer

    li $v0,8            # Syscall code to read a string
    syscall             # Load the string value read from console; $a0 = buffer, $a1 = length

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # Restore the stack pointer
    jr $ra              # Return to caller

# Read the char value from the user through console.
jminusminus.SPIM.readChar:

    subu $sp,$sp,32     # Stack frame is 32 bytes long
    sw $fp,28($sp)      # Save frame pointer
    addu $fp,$sp,32     # Set up frame pointer

    li $v0,12           # Syscall code to read a char
    syscall             # Load the char value read from console into $v0

    lw $fp,28($sp)      # Restore frame pointer
    addiu $sp,$sp,32    # Restore the stack pointer
    jr $ra              # Return to caller

# Exits SPIM.
jminusminus.SPIM.exit:

    li $v0,10           # Syscall code to exit
    syscall

# Exits SPIM with a specified code (in $a0).
jminusminus.SPIM.exit2:

    li $v0,17           # Syscall code to exit2
    syscall

