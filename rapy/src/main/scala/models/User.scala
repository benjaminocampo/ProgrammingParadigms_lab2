package models

trait User {
    val username: String   
    val locationId: Int
    protected var balance: Float = 0
    
    def increment(amount: Float): Unit = balance += amount
    def decrement(amount: Float): Unit = balance -= amount
}